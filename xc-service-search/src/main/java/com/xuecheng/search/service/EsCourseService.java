package com.xuecheng.search.service;


import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsCourseService {

    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;


    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 课程搜索(先实现按关键字搜索)
     *
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        //对参数进行判断
        if (courseSearchParam == null) {
            courseSearchParam = new CourseSearchParam();
        }
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索的类型
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //过滤源字段
        //把注入的source_field使用split切割为数组a
        String[] split = source_field.split(",");
        //查询需要查询出的字段名
        searchSourceBuilder.fetchSource(split, new String[]{});
        //搜索条件
        //根据关键字搜索
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            //匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder =
                    QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                            //设置匹配占比
                            .minimumShouldMatch("70%")
                            //提升另个字段的Boost值
                            .field("name", 10);

            //设置multiMatchQueryBuilder到boolQueryBuilder中去
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            //根据一级分类搜索
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            //根据二级分类搜索
            boolQueryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            //根据难度登等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }


        //设置boolQueryBuilder到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页参数
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 11;
        }
        //起始记录的下标
        int from = (page - 1) * size;
        //设置分页参数
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //向搜索请求中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //创建返回值的条件对象
        QueryResult<CoursePub> queryResult = new QueryResult<CoursePub>();
        List<CoursePub> list = new ArrayList<>();
        //使用restHighLevelClient进行检索
        try {
            //执行搜索
            SearchResponse search = restHighLevelClient.search(searchRequest);
            //获得响应结果
            SearchHits hits = search.getHits();
            //获取总记录数
            long totalHits = hits.getTotalHits();//总记录数
            queryResult.setTotal(totalHits);
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit searchHit : hits1) {
                //创建对象,封装数据
                CoursePub coursePub = new CoursePub();
                //获得源数据
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                //取出name
                String name = (String) sourceAsMap.get("name");
                //取出id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                //取出高亮字段name
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                if (highlightFields != null) {
                    HighlightField highlightFieldName = highlightFields.get("name");
                    //
                    if (highlightFieldName != null) {
                        Text[] fragments = highlightFieldName.fragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text fragment : fragments) {
                            stringBuffer.append(fragment);
                        }
                        name = stringBuffer.toString();
                    }
                }
                coursePub.setName(name);
                //取出图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                if (sourceAsMap.get("price") != null) {
                    price = (Double) sourceAsMap.get("price");
                }
                coursePub.setPrice(price);
                //原价
                Double oldprice = null;
                if (sourceAsMap.get("price_old") != null) {
                    oldprice = (Double) sourceAsMap.get("price_old");
                }
                coursePub.setPrice_old(oldprice);

                //每循环一次,就把创建的coursePub对象放进前边定义的list集合中
                list.add(coursePub);
            }
            //for循环执行完吧list放入queryResult
            queryResult.setList(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建返回值对象
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    /**
     * 根据id查询课程详情
     *
     * @param id
     * @return
     */
    public Map<String, CoursePub> getall(String id) {
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索的类型
        searchRequest.types(type);
        //定义SearchSourceBuilder对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件，根据课程id查询
        searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
        //取消source源字段过虑,查询所有字段
        //searchSourceBuilder.fetchSource(new String[]{"name","grade","charge","pic"},new String[]{});
        //封装查询对象
        searchRequest.source(searchSourceBuilder);

        Map<String, CoursePub> map = new HashMap<>();
        //使用api进行查询
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            //获取搜索结果
            SearchHits hits = search.getHits();
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit documentFields : hits1) {
                Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                CoursePub coursePub = new CoursePub();
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setGrade(grade);
                coursePub.setCharge(charge);
                coursePub.setPic(pic);
                coursePub.setDescription(description);
                coursePub.setTeachplan(teachplan);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 根据课程计划id查询索引(媒资信息)
     * @param teachplanIds id数组
     * @return QueryResponseResult
     */
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        //设置搜索的类型
        searchRequest.types(media_type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        String[] source_fields = media_source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields,new String[]{});

        //查询条件，根据课程计划id查询(可传入多个id)
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //封装查询对象
        searchRequest.source(searchSourceBuilder);
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        try {
            //查询
            SearchResponse search = restHighLevelClient.search(searchRequest);
            //获得查到的资源
            SearchHits hits = search.getHits();
            //获得查到的总记录数
            long totalHits = hits.getTotalHits();
            //获得所需要的数据
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                //创建对象用于封装数据
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                //封装数据
                teachplanMediaPub.setCourseId((String) sourceAsMap.get("courseid"));
                teachplanMediaPub.setMediaUrl((String) sourceAsMap.get("media_url"));
                teachplanMediaPub.setMediaFileOriginalName((String) sourceAsMap.get("media_fileoriginalname"));
                teachplanMediaPub.setMediaId((String) sourceAsMap.get("media_id"));
                teachplanMediaPub.setTeachplanId((String) sourceAsMap.get("teachplan_id"));
                //将数据加入列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
            //创建QueryResult对象--构建返回课程媒资信息对象
            QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
            queryResult.setTotal(totalHits);
            queryResult.setList(teachplanMediaPubList);
            //创建返回值对象
            QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS,queryResult);

            //返回
            return queryResponseResult;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
