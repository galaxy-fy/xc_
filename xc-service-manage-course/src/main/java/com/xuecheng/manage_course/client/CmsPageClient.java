package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "XC-SERVICE-MANAGE-CMS") //指定远程调用的服务名
public interface CmsPageClient {
    //定义方法,根据id查询页面信息,远程调用cms请求数据
    @GetMapping("/cms/get/{id}")//使用GetMapping标识远程调用的http的方法类型
    public CmsPage FeignfindById(@PathVariable("id") String id);

    //定义方法,远程调用cms的addOrUpdate接口进行保存页面,用于课程预览
    @PostMapping("/cms/save")
    public CmsPageResult addOrUpdate(@RequestBody CmsPage cmsPage);
    //远程调用cms的一键发布
    @PostMapping("/cms/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);

}
