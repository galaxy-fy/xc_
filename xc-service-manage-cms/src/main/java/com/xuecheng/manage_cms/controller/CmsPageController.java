package com.xuecheng.manage_cms.controller;


import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms")
public class CmsPageController implements CmsPageControllerApi {
    @Autowired
    private PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") Integer page, @PathVariable("size") Integer size, QueryPageRequest queryPageRequest) {
        /*//暂时采用测试数据，测试接口是否可以正常运行
        //创建一个queryResult对象
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(2);
        //静态数据列表
        List list = new ArrayList();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("IU");
        cmsPage.setPageCreateTime(new Date());
        list.add(cmsPage);
        queryResult.setList(list);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);*/
        return pageService.findList(page,size,queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.findById(id);
    }

    /**
     *
     * 修改页面
     * @param id
     * @param cmspage
     * @return
     */

    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult update(@PathVariable("id") String id,@RequestBody CmsPage cmspage) {
        return pageService.update(id,cmspage);
    }

    /**
     * 删除页面
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/del/{id}")
    public CmsPageResult delete(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    /**
     * 发送消息,存储静态化文件到GridFs
     * @param pageId
     * @return
     */
    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.postPage(pageId);
    }

    /**
     * 保存或新增页面
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/save")
    public CmsPageResult addOrUpdate(@RequestBody CmsPage cmsPage) {
        return pageService.addOrUpdate(cmsPage);
    }

    /**
     * 一键发布页面
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return pageService.postPageQuick(cmsPage);
    }
}
