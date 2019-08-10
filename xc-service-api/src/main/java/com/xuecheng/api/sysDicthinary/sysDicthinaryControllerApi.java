package com.xuecheng.api.sysDicthinary;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;

@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface sysDicthinaryControllerApi {
    public SysDictionary findDictionary(String type) ;
}
