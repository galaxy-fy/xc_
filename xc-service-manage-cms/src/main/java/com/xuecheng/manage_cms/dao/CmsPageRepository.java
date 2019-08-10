package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    /**
     * 命名规则查询
     */
    //根据name查询
    CmsPage findByPageName(String name);
    //根据页面名称和类型查询
    CmsPage findByPageNameAndPageType(String pageName,String pageType);
    //根据站点和页面类型查询记录数
    int countBySiteIdAndPageType(String siteId,String pageType);
    //根据站点和页面名称分页查询
    Page<CmsPage> findBySiteIdAndPageName(String siteId, String pageName, Pageable pageable);

    //根据页面名称、站点id、页面访问路径查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}
