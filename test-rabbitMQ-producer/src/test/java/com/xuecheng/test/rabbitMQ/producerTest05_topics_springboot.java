package com.xuecheng.test.rabbitMQ;

import com.xuecheng.test.rabbitMQ.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *  路由模式(TOPICS)
 *
 * 生产者测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class producerTest05_topics_springboot {
    @Autowired
    RabbitTemplate rabbitTemplate;

    //使用rabbitTemplate发送消息
    @Test
    public void testSendEmail(){
        //定义消息内容
        String  message = "send message to email, And I love You";
        /**
         * 参数内容:
         *  1.交换机名称
         *  2.routingKey
         *  3.消息内容
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);
    }

}
