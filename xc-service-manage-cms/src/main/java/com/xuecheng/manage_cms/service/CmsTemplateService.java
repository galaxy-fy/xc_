package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsTemplateService {
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    /**
     * 查找所有的模板信息.
     * @return
     */
    public QueryResponseResult findAll(){
        //调用方法查询结果
        List<CmsTemplate> all = cmsTemplateRepository.findAll();
        //创建结果对象,对查询结果进行封装
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all);
        queryResult.setTotal(all.size());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
