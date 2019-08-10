package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="Integer"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="Integer")
                    })
    public QueryResponseResult findList(Integer page, Integer size, QueryPageRequest queryPageRequest) ;
    //新增页面
    @ApiOperation("新增页面")
    public CmsPageResult add(CmsPage cmsPage);

    //根据id查询
    @ApiOperation("通过ID查询页面")
    public CmsPage findById(String id);

    //修改页面
    @ApiOperation("修改页面")
    public CmsPageResult update(String id,CmsPage cmspage);

    //删除页面
    @ApiOperation("删除页面")
    public CmsPageResult delete(String id);

    //页面发布
    @ApiOperation("发布页面")
    public ResponseResult post(String  pageId);

    @ApiOperation("添加页面")
    public CmsPageResult addOrUpdate(CmsPage cmsPage);

    @ApiOperation("一键发布")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
