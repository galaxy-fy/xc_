package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("all")//压制警告
public class CourseService {
    @Autowired
    CourseMapper courseMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired //远程调用
    CmsPageClient cmsPageClient;


    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;


    //课程计划查询
    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    /**
     * 添加课程计划
     * 原理:course_base 和teachplan两个表是分开的
     * course_base存储了用户新增的所有课程,
     * teacnplan存储了用户课程下的视频等相关文件
     * <p>
     * 场景1:
     * 用户点击新增,只是往course_base表中增加了一条数据,teacnplan表中没有这个数据,
     * 所以需要查询course_base,然后设置相关属性,新增到teacnplan表中
     * <p>
     * <p>
     * MySQL数据库需要添加事务控制
     *
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            //如果传过来的对象为null  或者 课程id为空  或者课程名称为空, 则不添加,直接抛出异常
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //取出课程id
        String courseid = teachplan.getCourseid();
        //取出页面传递的parentId
        String parentid = teachplan.getParentid();
        //如果parentid为空,就是前端没选择页面id,那么取出此节点的根节点,然后为parentid赋值
        if (StringUtils.isEmpty(parentid)) {
            //取出该课程的根节点
            parentid = this.getTeachPlanRoot(courseid);
        }
        //取出他的父节点
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        //取出父节点的Grade
        String parentGradeId = parentNode.getId();
        //new 一个新节点
        Teachplan teachplanNew = new Teachplan();
        //将页面提交的teachplan信息拷贝到teachplanNew中,使用BeanUtils工具类
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setCourseid(courseid);
        teachplanNew.setParentid(parentid);
        if (parentGradeId.equals("1")) {
            teachplanNew.setGrade("2");//级别设置(节点级别设置) -->通过父节点的grade来设置,他比父多1
        } else {
            teachplanNew.setGrade("3");//级别设置(节点级别设置) -->通过父节点的grade来设置,他比父多1
        }
        //保存
        teachplanRepository.save(teachplanNew);

        //处理parentid
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据课程id和parentId来查询数据库 返回parentid.
    private String getTeachPlanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        //课程信息
        CourseBase courseBase = optional.get();
        //根据id查询
        List<Teachplan> teachPlanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        //对查询出来的结果进行判断
        if (teachPlanList == null || teachPlanList.size() <= 0) {
            //没查到数据,要自动添加根节点
            //new一个对象用于存储数据
            Teachplan teachplan = new Teachplan();
            //为父节点设置ID: 0
            teachplan.setParentid("0");
            //级别为第一级
            teachplan.setGrade("1");
            //设置课程名称
            teachplan.setPname(courseBase.getName());
            //设置课程id
            teachplan.setCourseid(courseId);
            //设置status
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        //返回根节点id
        return teachPlanList.get(0).getId();
    }

    /**
     * 分页查询前台课程信息
     *
     * @param page              当前页
     * @param size              页面大小
     * @param courseListRequest 查询的相关参数
     * @return
     */
    public QueryResponseResult findCourseList(Integer page, Integer size, CourseListRequest courseListRequest) {
        //对传递参数进行判断
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        //对分页数据进行判断
        if (page <= 0 || "".equals(page) || page == null) {
            page = 1;
        }
        if (size <= 0 || "".equals(size) || size == null) {
            size = 8;
        }
        //设置分页参数
        PageHelper.startPage(page, size);
        //进行分页的数据库查询
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        //得到分页的数据
        List<CourseInfo> result = courseListPage.getResult();
        //总记录数
        long total = courseListPage.getTotal();
        //查询结果集
        QueryResult<CourseInfo> queryResult = new QueryResult<CourseInfo>();
        //对QueryResult进行封装
        queryResult.setList(result);
        queryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 添加课程信息
     *
     * @param courseBase
     * @return
     */
    @Transactional//事务控制
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        //对数据进行控制
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
       /* //查询数据库看是否能得到信息
        courseBaseRepository.findById()*/
        //把页面设置为未发布状态
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }

    /**
     * 根据id查询课程信息
     *
     * @param id
     * @return
     */
    public CourseBase findById(String id) {
        if (id == null || "".equals(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CourseBase> byId = courseBaseRepository.findById(id);
        if (byId.isPresent()) {
            CourseBase courseBase = byId.get();
            return courseBase;
        }
        return null;
    }

    @Transactional//更新操作加事务注解
    @Rollback(false)//更新操作默认执行回滚操作,需要设置rollback为false
    public ResponseResult update(String id, CourseBase courseBase) {
        CourseBase byid = this.findById(id);
        if (byid == null) {
            ExceptionCast.cast(CommonCode.COURSE_ERROR);
        }
        //修改课程
        byid.setName(courseBase.getName());
        byid.setMt(courseBase.getMt());
        byid.setSt(courseBase.getSt());
        byid.setGrade(courseBase.getGrade());
        byid.setStudymodel(courseBase.getStudymodel());
        byid.setUsers(courseBase.getUsers());
        byid.setDescription(courseBase.getDescription());
        CourseBase save = courseBaseRepository.save(byid);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询课程营销信息
     *
     * @param id
     * @return
     */
    public CourseMarket getCourseMarketById(String id) {
        Optional<CourseMarket> byId = courseMarketRepository.findById(id);
        if (byId.isPresent()) {
            //如果有值
            CourseMarket courseMarket = byId.get();
            return courseMarket;
        }
        return null;
    }


    /**
     * 修改课程营销信息
     * 事务处理,
     * 不回滚
     *
     * @param id
     * @param courseMarket
     * @return
     */
    @Transactional
    @Rollback(false)
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket courseMarketById = this.getCourseMarketById(id);
        //判断, 为空 没有值
        if (courseMarketById == null) {
            //如果他没有值,就新创建个对象赋值给他
            courseMarketById = new CourseMarket();
            //把id传给他
            courseMarketById.setId(id);
            //copy内容
            BeanUtils.copyProperties(courseMarket, courseMarketById);
            //存储
            CourseMarket save = courseMarketRepository.save(courseMarketById);
            //返回成功
            return new ResponseResult(CommonCode.SUCCESS);
        }

        //有值,更新
        courseMarketById.setCharge(courseMarket.getCharge());
        courseMarketById.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
        courseMarketById.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
        courseMarketById.setPrice(courseMarket.getPrice());
        courseMarketById.setQq(courseMarket.getQq());
        courseMarketById.setValid(courseMarket.getValid());
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 保存课程图片的信息
     * @param courseId
     * @param pic
     * @return
     */
    @Transactional
    @Rollback(false)
    public ResponseResult addCoursePic(String courseId, String pic) {
        //声明一个空变对象
        CoursePic coursePic = null;
        //查询课程图片
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        /**
         * 如果有值就取出来赋值,没有new一个对象
         */
        if (byId.isPresent()) {
            coursePic = byId.get();
        }
        //如果还是null 就new一个对象给他
        if(coursePic==null){
            coursePic = new CoursePic();
        }

        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        //调用dao保存
        coursePicRepository.save(coursePic);
        //返回信息
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询图片
     * @param courseId 图片id
     * @return
     */
    public CoursePic findPicByCourseId(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if(byId.isPresent()){
            return byId.get();
        }
        return null;
    }


    /**
     * 删除图片, 需要加事务控制
     * @param courseId
     * @return
     */
    @Transactional
    @Rollback(false)
    public ResponseResult deletePic(String courseId) {
        long l = coursePicRepository.deleteByCourseid(courseId);
        if(l>=0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 查询课程所有信息
     * @param id 课程id
     * @return
     */
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if(optionalCourseBase.isPresent()){
            courseView.setCourseBase(optionalCourseBase.get());
        }

        //查询课程图片信息
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(id);
        if(optionalCoursePic.isPresent()){
            courseView.setCoursePic(optionalCoursePic.get());
        }

        //查询课程营销信息
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);
        if(optionalCourseMarket.isPresent()){
            courseView.setCourseMarket(optionalCourseMarket.get());
        }

        //查询课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    /**
     * 课程预览
     * @param courseId 课程id
     * @return
     */
    public CoursePublishResult preview(String courseId) {
        CourseBase byId = this.findById(courseId);
        //请求cms添加页面
        //准备cmsPage数据
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//课程预览站点id
        cmsPage.setTemplateId(publish_templateId);//模板
        cmsPage.setPageName(courseId+".html");//页面名称
        cmsPage.setPageAliase(byId.getName());//页面别名
        cmsPage.setPageWebPath(publish_page_webpath);//页面访问路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面存储(物理)路径
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);//数据模型url
        //远程调用cmsPage
        CmsPageResult cmsPageResult = cmsPageClient.addOrUpdate(cmsPage);
        if(!cmsPageResult.isSuccess()){
            //如果不成功
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //拿到pageId
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //拼装页面预览的url
        String url=previewUrl+pageId;
        //返回CoursePublishResult对象(当中包含了页面预览的url)
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    /**
     * 课程发布
     * @param courseId
     * @return
     */
    public CoursePublishResult publish(String courseId) {
        CourseBase byId = this.findById(courseId);
        //请求cms添加页面
        //准备cmsPage数据
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//课程预览站点id
        cmsPage.setTemplateId(publish_templateId);//模板
        cmsPage.setPageName(courseId+".html");//页面名称
        cmsPage.setPageAliase(byId.getName());//页面别名
        cmsPage.setPageWebPath(publish_page_webpath);//页面访问路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面存储(物理)路径
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);//数据模型url
        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        //保存课程的发布状态为"已发布"
        CourseBase courseBase = this.saveCoursePubState(courseId);
        //课程索引...
        //课程缓存...
        //从远程调用接口的返回值中取出页面的预览路径(页面url)
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

        //设置页面的发布状态为"已发布"
    private CourseBase saveCoursePubState(String courseId){
        CourseBase byId = this.findById(courseId);
        //更新课程状态
        byId.setStatus("202002");
        CourseBase save = courseBaseRepository.save(byId);
        return save;
    }
}
