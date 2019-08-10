package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    RestClient restClient;

    /**
     * //创建索引库
     * @throws Exception
     */
    @Test
    public void testCreateIndex() throws Exception{
        //创建索引请求对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));

        //设置映射
        createIndexRequest.mapping("doc","{\n" +
                " \t\"properties\": {\n" +
                        "           \"name\": {\n" +
                        "              \"type\": \"text\",\n" +
                        "              \"analyzer\":\"ik_max_word\",\n" +
                        "              \"search_analyzer\":\"ik_smart\"\n" +
                        "           },\n" +
                        "           \"description\": {\n" +
                        "              \"type\": \"text\",\n" +
                        "              \"analyzer\":\"ik_max_word\",\n" +
                        "              \"search_analyzer\":\"ik_smart\"\n" +
                        "           },\n" +
                        "           \"studymodel\": {\n" +
                        "              \"type\": \"keyword\"\n" +
                        "           },\n" +
                        "           \"price\": {\n" +
                        "              \"type\": \"float\"\n" +
                        "           }\n" +
                        "        }\n" +
                        "}", XContentType.JSON);
        //创建索引操作客户端
        IndicesClient indicesClient = restHighLevelClient.indices();
        //创建响应对象
        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest);
        //得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }


    /**
     * //删除索引库
     * @throws Exception
     */
    @Test
    public void testDeleteIndex() throws Exception {
        //删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //删除索引
        DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(deleteIndexRequest);
        //删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }


    /**
     * //添加文档
     * @throws Exception
     */
    @Test
    public void testAddDoc() throws Exception{
        //参数设置
        Map<String,Object> jsonMap =new HashMap<String,Object>();
        jsonMap.put("name","spring cloud实战");
        jsonMap.put("description","本课程主要从四个章节进行讲解：1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring" +
                "Boot 4.注册中心eureka。");
        jsonMap.put("studymodel","201001");
        jsonMap.put("price",5.6);

        //创建索引请求对象
        /**
         * 参数1: 索引库对象
         * 参数2: 类型
         */
        IndexRequest indexRequest = new IndexRequest("xc_course","doc");
        //指定索引文档内容
        indexRequest.source(jsonMap);
        //通过client进行http的请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);

    }

    /**
     * //查询文档
     * @throws Exception
     */
    @Test
    public void testGetDoc() throws  Exception {
        //查询请求对象
        GetRequest getRequest = new GetRequest("xc_course", "doc", "Qk1GemwB_Xue0G3t-NQ0");
        GetResponse documentFields = restHighLevelClient.get(getRequest);
        //得到文档的内容
        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    /**
     * //修改文档(局部更新)
     * @throws Exception
     */
    @Test
    public void testUpdateDoc() throws Exception{
        //获取更新对象--->执行原理先检索到文档、将原来的文档标记为删除、创建新文档、删除旧文档，创建新文档就会重建索引。
        UpdateRequest updateRequest = new UpdateRequest("xc_course", "doc", "Qk1GemwB_Xue0G3t-NQ0");
        //准备更新的数据
        Map<String,Object> map = new HashMap<>();
        map.put("name","ElasticSearch测试");
        //绑定数据
        updateRequest.doc(map);
        //执行更新
        UpdateResponse update = restHighLevelClient.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }

    /**
     * //修改文档(局部更新)
     * @throws Exception
     */
    @Test
    public void testUpdateDoc1() throws Exception{
        //获取更新对象,绑定指定的索引
        UpdateRequest updateRequest = new UpdateRequest("xc_course","doc","Qk1GemwB_Xue0G3t-NQ0");
        //准备数据
        Map<String,Object> map = new HashMap<>();
        map.put("name","去看星星吗");
        map.put("price",6.6);
        map.put("studymodel","201002");
        map.put("description","脸红的思春期");
        //绑定数据
        updateRequest.doc(map);
        //执行更新
        UpdateResponse update = restHighLevelClient.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }

    /**
     * //删除文档
     * @throws Exception
     */
    @Test
    public void testDelDoc() throws Exception{
        //删除文档的id
        String id="Qk1GemwB_Xue0G3t-NQ0";

        //删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("xc_course","doc",id);
        //响应对象
        DeleteResponse  deleteResponse = restHighLevelClient.delete(deleteRequest);
        RestStatus status = deleteResponse.status();
        System.out.println(status);
    }
}
