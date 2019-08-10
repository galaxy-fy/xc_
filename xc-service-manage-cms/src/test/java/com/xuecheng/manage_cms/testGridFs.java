package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testGridFs {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    /**
     * 上传模板
     * @throws FileNotFoundException
     */
    @Test
    public void testGridFsPush() throws FileNotFoundException {
        //要存储的文件
        File file = new File("F:/index_banner.ftl");
        //定义输入流
        FileInputStream fis = new FileInputStream(file);
        //向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(fis, "轮播图测试文件01");
        //得到文件ID
        String s = objectId.toString();
        System.out.println(s);
    }
    /**
     * 上传模板
     */
    @Test
    public void testGridFsPushCourse() throws FileNotFoundException {
        //要存储的文件
        File file = new File("D:/course.ftl");
        //定义输入流
        FileInputStream fis = new FileInputStream(file);
        //向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(fis, "课程详情页面");
        //得到文件ID
        String s = objectId.toString();
        System.out.println(s);
    }

    /**
     * 下载模板
     * @throws FileNotFoundException
     */
    @Test
    public void testGridFsPull() throws IOException {
        //给出id
        String fileId="5d443f0ef921c321a411c520";
        //根据id去数据库取文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取流中的数据
        String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(content);
    }
}
