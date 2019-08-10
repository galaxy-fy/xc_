package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class pageService {

    //根据日志来排错
    private static final Logger LOGGER = LoggerFactory.getLogger(pageService.class);

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired//操作流对象
    GridFSBucket gridFSBucket;

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;

    //保存下载文件到具体的服务器的物理路径
    public void savePageToServerPath(String pageId) {
        //获得页面的信息(根据id查询)
        CmsPage cmsPage = this.getPageById(pageId);
        //得到html文件的id,从cmsPage中获取htmlFileId内容
        String htmlFileId = cmsPage.getHtmlFileId();
        //从GridFS中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if(inputStream==null){
            LOGGER.error("getFileById return InputStream is null,htmlFileId:{}"+htmlFileId);
            return;
        }
        //获得站点的id
        String siteId = cmsPage.getSiteId();
        //获得站点的信息
        CmsSite cmsSite = getCmsSiteById(siteId);
        //得到站点的物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //得到页面的物理路径
        String pagePath= sitePhysicalPath+ cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
        //将html文件保存到物理路径
            //定义一个输出流
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    //定义一个方法,得到站点的物理路径
    public CmsSite getCmsSiteById(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    //定义一个方法,根据文件的id从GridFS中查询html文件
    public InputStream getFileById(String fileId) {
        //获得文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream=gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFsResource,
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //定义一个方法,用于获得页面的详细信息
    public CmsPage getPageById(String pageId){
        //获取页面的详细信息
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
