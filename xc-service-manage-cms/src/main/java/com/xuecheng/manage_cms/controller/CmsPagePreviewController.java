package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

//因为不需要向页面返回json数据,所以这里使用Controller注解即可
@Controller
public class CmsPagePreviewController extends BaseController{

    //注入service,直接调用service的方法即可
    @Autowired
    PageService pageService;

    //使用BaseController的response来对结果进行返回,所以这里方法不需要返回值
    @RequestMapping(value = "/cms/preview/{pageId}",method= RequestMethod.GET)
    //接收到页面id
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        String pageHtml = pageService.getPageHtml(pageId);
        //使用response对象获取流,然后对service返回的数据进行操作
        ServletOutputStream outputStream = response.getOutputStream();
        //设置响应头(Nginx解析html页面,但是返回的是flt的文件)
        response.setHeader("Content-type","text/html;charset=utf-8");
        outputStream.write(pageHtml.getBytes("utf-8"));
    }
}
