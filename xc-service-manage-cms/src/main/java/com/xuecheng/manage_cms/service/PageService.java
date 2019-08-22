package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired//发送消息用到
            RabbitTemplate rabbitTemplate;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**
     * 分页查询
     * 参数问题
     *
     * @param page             前台传递过来的页面参数,前台页面需要从1页开始,数据库是从0页开始,所以需要对参数的值进行相关操作
     * @param size             前台传递过来的页面条数参数.
     * @param queryPageRequest 自定义的类
     * @return
     */
    public QueryResponseResult findList(Integer page, Integer size, QueryPageRequest queryPageRequest) {
        //对传递过来的queryPageRequest进行判断
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        //自定义条件查询
        //定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //如果站点ID不为空
        //设置站点id作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置页面别名作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        //对前台传递的参数进行判断
        //如果当前页小于0  那么让他等于第一页
        if (page <= 0 || page == null || "".equals(page)) {
            page = 1;
        }
        //如果当前每页显示的条数小于0,那么让他默认等于5
        page = page - 1;
        if (size <= 0 || size == null || "".equals(size)) {
            size = 5;
        }

        //定义Example条件对象
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        //new一个pageable进行分页条件限制
        Pageable pageable = new PageRequest(page, size);
        //查询数据库时添加分页条件
        /**
         * 实现自定义条件分页查询
         */
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        //一个QueryResult进行结果的封装
        QueryResult queryResult = new QueryResult();
        //封住哪个查询出来的总记录数
        queryResult.setTotal(all.getTotalElements());
        //封装查询出来数据
        queryResult.setList(all.getContent());
        //new 一个QueryResponseResult对象封装查询出来的值 并且返回
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }


    /**
     * 添加方法
     *
     * @param cmsPage 页面参数
     * @return 操作结果
     * */
    /*
     public CmsPageResult add(CmsPage cmsPage) {
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //对查询结果进行判断, 如果对象为null,数据库就没有这个对象,
        if (page == null) {
            cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
            //调用dao的save方法
            cmsPageRepository.save(cmsPage);
            //返回结果
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }*/

    /**
     * 添加方法
     *
     * @param cmsPage 页面参数
     * @return 操作结果
     */
    public CmsPageResult add(CmsPage cmsPage) {
        //校验cmsPage是否为空
        if (cmsPage == null) {

        }
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //校验页面是否存在，已存在则抛出异常
        if (page != null) {
            //抛出异常，已存在相同的页面名称
            //抛出异常
            //异常内容是页面已存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
        //调用dao的save方法
        cmsPageRepository.save(cmsPage);
        //返回结果
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        return cmsPageResult;
    }


    /**
     * 根据id查询page对象
     */
    public CmsPage findById(String id) {
        //根据id查询数据库
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        //对取到的结果进行判断
        if (byId.isPresent()) {
            //如果对象有值
            CmsPage cmsPage = byId.get();
            return cmsPage;
        }
        //返回空
        return null;
    }

    /**
     * 更新页面信息
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage one = this.findById(id);
        //如果查找到的one有值
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新DataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            cmsPageRepository.save(cmsPage);
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    //删除方法
    public CmsPageResult delete(String id) {
        //先根据id查询
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        //对查询结果进行判断
        if (byId.isPresent()) {
            //如果有值
            CmsPage cmsPage = byId.get();
            cmsPageRepository.delete(cmsPage);
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    //页面静态化方法

    /**
     * 静态化程序获取页面的DataUrl
     * <p>
     * 静态化程序远程请求DataUrl获取数据模型。
     * <p>
     * 静态化程序获取页面的模板信息
     * <p>
     * 执行页面静态化
     */
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null) {
            //数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //获取页面的模板信息
        String template = getTemplateByPageId(pageId);
        if (template == null) {
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //执行静态化
        String staticHtml = getStaticHtml(model, template);
        return staticHtml;
    }

    //执行静态化方法
    public String getStaticHtml(Map model, String templateContent) {
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String staticHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return staticHtml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取页面的模板信息
    private String getTemplateByPageId(String pageId) {
        //取出页面的信息
        CmsPage cmsPage = this.findById(pageId);
        //非空判断
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面的模板id
        String templateId = cmsPage.getTemplateId();
        //非空判断
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        //非空判断
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFS中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //获取数据模型的方法
    private Map getModelByPageId(String pageId) {
        //获取页面信息
        CmsPage cmsPage = this.findById(pageId);
        //非空判断
        if (cmsPage == null) {
            //页面信息为空,即页面不存在
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        //取出页面的dataUrl
        String dataUrl = cmsPage.getDataUrl();
        //非空判断
        if (dataUrl == null) {
            //dataUrl为空,
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        //取出数据
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * 页面发布
     */
    public ResponseResult postPage(String pageId) {
        //执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            //如果为空就抛出参数异常错误
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //保存静态化文件
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        //发送消息
        this.sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //发送消息的方法
    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        //new一个map用来存储消息信息
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);
        //消息内容
        String msg = JSON.toJSONString(msgMap);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();
        //发送消息--指定交换机名称,routingKey,发布的消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);
    }


    //保存静态页面内容的方法
    private CmsPage saveHtml(String pageId, String pageContent) {
        //查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();
        //存储之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //将pageContent内容转换为输入流
        InputStream inputStream = IOUtils.toInputStream(pageContent);
        //保存html文件到GridFS返回一个ObjectId
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //文件id
        String fileId = objectId.toString();
        //将文件id存储到cmsPage中的HtmlFileId
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    /**
     * 更新或者新增页面
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult addOrUpdate(CmsPage cmsPage) {
        //调用dao进行查询
        CmsPage cmspage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmspage1 == null) {
            //如果没有值就调用新增方法
            return this.add(cmsPage);

        }
        //有就更新
        return this.update(cmspage1.getPageId(), cmspage1);
    }

    /**
     * 一键发布
     *
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //调用写的(更新或新增)方法
        CmsPageResult cmsPageResult = this.addOrUpdate(cmsPage);
        //如果不成功就返回空
        if (!cmsPageResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        //得到要布的页面id
        String pageId = cmsPage1.getPageId();
        //发布页面
        ResponseResult responseResult = this.postPage(pageId);
        //发布是否成功进行判断
        if (!responseResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        //得到站点id
        String siteId = cmsPage1.getSiteId();
        //查询站点信息
        CmsSite cmsSite = this.findSiteBySiteId(siteId);
        if (cmsSite == null) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        //拼接dataUrl返回前端
        /**
         *  页面url=站点域名+站点webpath+页面webpath+页面名称
         *  http://www.xuecheng.com/course/detail/4028e581617f945f01617f9dabc40000.html
         */

        String dataUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + cmsPage1.getPageWebPath()+cmsPage1.getPageName();

        return new CmsPostPageResult(CommonCode.SUCCESS,dataUrl);
    }

    /**
     * 根据站点id查询站点信息
     *
     * @param siteId
     * @return
     */
    public CmsSite findSiteBySiteId(String siteId) {
        Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(siteId);
        if (cmsSiteOptional.isPresent()) {
            return cmsSiteOptional.get();
        }
        return null;
    }
}