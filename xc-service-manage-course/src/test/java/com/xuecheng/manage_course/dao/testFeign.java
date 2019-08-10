package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testFeign {
    @Autowired
    CmsPageClient cmsPageClient; //接口代理对象,由Feign生成代理对象
    @Test
    public void TestMyFeign(){
        //发起远程调用
        CmsPage cmsPage = cmsPageClient.FeignfindById("5d44656af921c31d38d61428");
        System.out.println(cmsPage);
    }

}
