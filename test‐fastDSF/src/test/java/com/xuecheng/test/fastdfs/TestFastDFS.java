package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {
    //上传测试
    @Test
    public void testUpload(){
        try {
            //加载fastdfs-client.properties配置文件(默认在classpath下)
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient,用于请求TrackerServer
            TrackerClient trackerClient  = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer =trackerClient.getConnection();
            //获取Storage
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //向Storage服务器上传文件
            //本地文件的路径
            String filePath="D:/Demo/1.jpg";
            //上传成功后拿到文件的id
            /**
             * 参数1: 文件的本地路径
             * 参数2: 文件的后缀名
             * 参数3: 文件的信息
             */
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);
            //上传的文件的id
            //group1/M00/00/01/wKgZmV1JC4KAEZylAALa50Bllio424.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //下载测试
    @Test
    public void testDownLoad(){
        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建TrackerClient用于连接TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接Tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建StorageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //下载文件 获得一个byte数组
            //定义文件的id
            String fileId="group1/M00/00/01/wKgZmV1JC4KAEZylAALa50Bllio424.jpg";
            byte[] bytes = storageClient1.download_file1(fileId);
            //输出流...
            FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/storage/1.jpg"));
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    //查询文件
    @Test
    public void testQuery(){
        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建TrackerClient用于连接TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接Tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建StorageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //查询文件(根据id)
            FileInfo fileInfo = storageClient1.query_file_info1("group1/M00/00/01/wKgZmV1JC4KAEZylAALa50Bllio424.jpg");
            System.out.println(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
