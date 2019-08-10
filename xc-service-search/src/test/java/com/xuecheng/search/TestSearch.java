package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    RestClient restClient;

    /**
     * 搜索全部
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //设置搜索参数
        //matchAllQuery搜索全部
        searchRequestBuilder.query(QueryBuilders.matchAllQuery());
        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );

        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);

        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //文档的主键
            String id = searchHit.getId();
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            float score = searchHit.getScore();
            String sourceAsString = searchHit.getSourceAsString();
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    /**
     * 分页查询
     * @throws Exception
     */
    @Test
    public void testSearchPage() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //设置搜索参数
        //matchAllQuery搜索全部
        searchRequestBuilder.query(QueryBuilders.matchAllQuery());
        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );
        /**
         * 设置分页参数
         */
        int page = 1;
        int size = 1;
        int from  = (page-1)*size;
        searchRequestBuilder.from(from);
        searchRequestBuilder.size(size);
        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);

        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    /**
     * 精确查询
     * @throws Exception
     */
    @Test
    public void testTermQuery() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //设置搜索参数
        //matchAllQuery搜索全部
        searchRequestBuilder.query(QueryBuilders.termQuery("name","spring"));
        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );
        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);
        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    /**
     * 根据id查询
     * @throws Exception
     */
    @Test
    public void testTermQueryById() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //设置搜索参数
        String[] ids= new String[]{"1","2"};
        //matchAllQuery搜索全部
        searchRequestBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );
        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);
        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    /**
     * MatchQuery 全文检索
     * 先分词,再进行检索
     * 根据name查询
     * @throws Exception
     */
    @Test
    public void testMatchQuery() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //MatchQuery搜索包含关键词的文章
        searchRequestBuilder.query(QueryBuilders.matchQuery("description","Spring开发框架")
                /*.operator(Operator.AND)*/
                .minimumShouldMatch("80%")
        );
        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );
        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);
        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }


    /**
     * MultiQuery
     * @throws Exception
     */
    @Test
    public void testMultiQuery() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //MultiQuery
        searchRequestBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
                .minimumShouldMatch("50%")
                .field("name",10)
        );
        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );
        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);
        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }


    /**
     * BoolQuery
     * @throws Exception
     */
    @Test
    public void testBoolQuery() throws Exception{
        //请求搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        //BoolQuery查询



        //先定义一个multiMatchQuery查询对象
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        //再定义一个TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //定义一个BoolQuery查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //把查询条件装进去
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);

        //最后搜索的时候把boolQuery查询对象塞进去
        searchRequestBuilder.query(boolQueryBuilder);



        //source源字段过滤,第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchRequestBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp","description"},new String[]{} );
        //向搜索请求中设置搜索源
        searchRequest.source(searchRequestBuilder);
        //执行搜索,向ES发送http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索的结果
        SearchHits hits = searchResponse.getHits();
        //搜索到的总记录数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        //日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            //name字段的内容
            String name = (String) sourceAsMap.get("name");
            //前边之前没有设置查询这个字段,所以查询出来的结果不包含这个字段(后已补上)
            String description = (String)sourceAsMap.get("description");
            //学习模板
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }
}
