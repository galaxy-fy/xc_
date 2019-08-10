package com.xuecheng.test.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * 生产者测试
 */
public class producerTest {
    //队列名称
    public static final String QUEUE= "Galaxy";

    public static void main(String[] args) {
        //创建连接对象
        ConnectionFactory connectionFactory=new ConnectionFactory();
        //设置参数 主机名
        connectionFactory.setHost("127.0.0.1");
        //端口 -- 15672是web访问的端口, 实际的端口是5672
        connectionFactory.setPort(5672);
        //设置用户名
        connectionFactory.setUsername("guest");
        //设置密码
        connectionFactory.setPassword("guest");
        //设置虚拟机 -- rabbitmq默认虚拟机名称为“/”，虚拟机相当于一个独立的mq服务器
        connectionFactory.setVirtualHost("/");
        //创建与RabbitMQ服务的TCP连接
        Connection connection = null;
        //创建通道
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            //创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
            channel = connection.createChannel();
            /**
              * 声明队列，如果Rabbit中没有此队列将自动创建

                参数:String queue,boolean durable,boolean exclusive,boolean autoDelete, map<String,Object> arguments
                          * queue:队列名称
                          * durable:是否持久化
                          * exclusive:队列是否独占此连接
                          * autoDelete:队列不再使用时是否自动删除此队列
                          * arguments:队列参数
             */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //创建消息体
            String message = "Hello,Galaxy"+new Date().getTime();
            /**
                          * 消息发布方法
                          * param1：Exchange的名称，如果没有指定，则使用Default Exchange
                          * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
                          * param3:消息包含的属性
                          * param4：消息体
                          */
            /**
                          * 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显
             示绑定或解除绑定
                          * 默认的交换机，routingKey等于队列名称
                          */
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println(" send msg "+message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
