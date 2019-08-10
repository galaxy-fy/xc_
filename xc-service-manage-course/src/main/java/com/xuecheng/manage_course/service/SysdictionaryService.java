package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_course.dao.SysDictionaryDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysdictionaryService {
    @Autowired
    SysDictionaryDao sysDictionaryDao;
    //根据字典分类type查询字典信息
    public SysDictionary findDictionaryByType(String dType){
        if(StringUtils.isEmpty(dType)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询数据库获得所需要的数据
        return sysDictionaryDao.findByDType(dType);

    }
}
