package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTemplateTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testRestTemplate(){
        //调用方法获得响应结果
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/getmodel/5a791725dd573c3574ee333f", Map.class);
        //得到需要的分页数据
        Map body = forEntity.getBody();
        System.out.println(body);
    }
}
