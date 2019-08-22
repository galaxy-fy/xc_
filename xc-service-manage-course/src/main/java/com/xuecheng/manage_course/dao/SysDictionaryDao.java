package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 查询字典分类
 */
public interface SysDictionaryDao extends MongoRepository<SysDictionary,String> {
    SysDictionary findByDType(String dType);
}