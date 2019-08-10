package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    CmsPageRepository cmsPageRepository;

    //测试
    @Test
    public void testFind() {
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }


    //分页测试
    @Test
    public void testFindPage() {
        int page = 0;//从0开始
        int size = 10;//每页记录数
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    //添加
    @Test
    public void testInsert() {
        //定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    //删除
    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5b17a2c511fe5e0c409e5eb3");
    }

    //修改
    @Test
    public void testUpdate() {
        Optional<CmsPage> optional = cmsPageRepository.findById("5d351bb6424ce33268a4c7ff");
        /**
         * Optional是一个容器对象,主要目的是提醒进行非空判断.
         isPresent():不空返回true
         get():取出里边数据
         */
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("哈哈哈");
            cmsPageRepository.save(cmsPage);
        }
    }


    /**
     * 测试自定义dao方法
     */
    //根据name查询
    @Test
    public void testFindByPageName() {
        CmsPage cmsPage = cmsPageRepository.findByPageName("哈哈哈");
        System.out.println(cmsPage);
    }

    //根据页面名称和类型查询
    @Test
    public void testfindByPageNameAndPageType() {
        CmsPage byPageNameAndPageType = cmsPageRepository.findByPageNameAndPageType("4028e58161bd3b380161bd3bcd2f0000.html", "1");
        System.out.println(byPageNameAndPageType);
    }

    //根据站点和页面类型查询记录数
    @Test
    public void testcountBySiteIdAndPageType() {
        int count = cmsPageRepository.countBySiteIdAndPageType("5a751fab6abb5044e0d19ea1", "1");
        System.out.println(count);
    }

    //根据站点和页面名称分页查询
    @Test
    public void testfindBySiteIdAndPageName() {
        Pageable pageable = new PageRequest(0, 3);
        Page<CmsPage> bySiteIdAndPageNameLike = cmsPageRepository.findBySiteIdAndPageName("5a751fab6abb5044e0d19ea1", "4028e58161bd3b380161bd3bcd2f0000.html", pageable);
        for (CmsPage cmsPage : bySiteIdAndPageNameLike) {
            System.out.println(cmsPage);
        }
    }


    /**
     *  自定义条件查询测试
     *       根据分页和条件查询
     *  ExampleMatcher:条件匹配器
     */
    @Test
    public void testExample() {
        //分页参数
        int page = 0;//从0开始
        int size = 10;//每页记录数
        Pageable pageable = PageRequest.of(page, size);
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //为对象赋值(根据查询)
        cmsPage.setPageAliase("面");
        //根据站点id查询
        //cmsPage.setSiteId("5b30b052f58b4411fc6cb1cf");
        //根据模板id查询
        //cmsPage.setTemplateId("5ad9a24d68db5239b8fef199");
        //创建条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        /**
         * 使用withMatcher方法,传递参数
         * !!!!!!
         *
         *      每次withMatcher都会重新生成一个ExampleMatcher对象,需要接收,不然没用
         *
         *  参数1: 需要进行匹配的实体类的属性名称
         *  参数2: 进行查询的方式
         *     ExampleMatcher.GenericPropertyMatchers:
         *          contains:包含
         *          startWith:开头
         *          endWith:结尾
         *          exatch:精确匹配
         */
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //定义example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }
}
