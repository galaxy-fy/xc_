package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsSiteService {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 查询所有的站点信息
     * @return
     */
    public QueryResponseResult findAll(){
        //调方法查询到所有的站点信息
        List<CmsSite> all = cmsSiteRepository.findAll();
        //创建对象封装数据
        QueryResult queryResult= new QueryResult();
        queryResult.setList(all);
        queryResult.setTotal(all.size());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
