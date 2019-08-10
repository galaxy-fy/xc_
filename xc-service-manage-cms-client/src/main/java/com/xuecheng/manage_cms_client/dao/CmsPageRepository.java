package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;
//通过CmsPageRepository查询页面信息
public interface CmsPageRepository extends MongoRepository<CmsPage,String>{
}
