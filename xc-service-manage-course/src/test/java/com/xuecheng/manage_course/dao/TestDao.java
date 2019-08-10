package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import com.xuecheng.manage_course.service.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    SysDictionaryDao sysDictionaryDao;

    @Autowired
    CourseService courseService;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);

    }

    //测试课程计划查询
    @Test
    public void testTeachPlan(){
        TeachplanNode teachplanNode = teachplanMapper.selectList("4028e581617f945f01617f9dabc40000");
        System.out.println(teachplanNode);

    }


    //分页助手测试
    @Test
    public void  testPageHelper(){
        //设置分页参数
        PageHelper.startPage(1,10);
        CourseListRequest courseListRequest = new CourseListRequest();
        courseListRequest.setCompanyId("1");
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> result = courseListPage.getResult();
        System.out.println(result);
    }

    //字典分类type查询字典信息测试
    @Test
    public void testSysdictionary(){
        //设置一个type
        String type="200";
        SysDictionary byDType = sysDictionaryDao.findByDType(type);
        List<SysDictionaryValue> dValue = byDType.getDValue();
        System.out.println(dValue);
    }

    /**
     * 修改课程信息测试
     */
    @Test
    @Transactional
    @Rollback(false)
    public void testUpdate() {
        CourseBase byId = courseService.findById("297ecc816c6073ef016c60746f0c0000");
        byId.setName("111");
        courseService.update("297ecc816c6073ef016c60746f0c0000",byId);
    }
}
