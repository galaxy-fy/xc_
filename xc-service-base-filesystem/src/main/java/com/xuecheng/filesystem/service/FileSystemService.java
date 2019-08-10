package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileSystemService {
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;
    /**
     * 文件上传
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务标识
     * @param metadata 元数据
     * @return
     */
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata){

        //如果文件为null,不继续进行
        if(multipartFile==null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //第一步:将文件上传到fastDFS,得到一个文件id
        String fileId = upload_files(multipartFile);
        //创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(fileId);
        //文件在文件系统中的路径
        fileSystem.setFilePath(fileId);
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //元数据
        if(StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        //名称
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        //大小
        fileSystem.setFileSize(multipartFile.getSize());
        //文件类型
        fileSystem.setFileType(multipartFile.getContentType());
        //第二步:将文件id及其他文件的信息存储到MongoDB数据库中
        fileSystemRepository.save(fileSystem);
        //返回成功
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);

    }


    //上传文件到fastDFS,返回文件的id
    private String upload_files(MultipartFile multipartFile){
        try {
            //加载fdfs的配置
            init();
            //创建TrackerClient用于连接trackerServer
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            //连接storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storage client
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //上传文件
            //获得文件的byte数组
            byte[] bytes = multipartFile.getBytes();
            //获得文件原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //文件的扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            //上传成功后获取文件的id
            String fileId = storageClient1.upload_file1(bytes, extName, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //初始化方法
    public void init(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
