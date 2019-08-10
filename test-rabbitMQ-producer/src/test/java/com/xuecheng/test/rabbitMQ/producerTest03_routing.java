package com.xuecheng.test.rabbitMQ;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *  路由模式
 *
 * 生产者测试
 */
public class producerTest03_routing {
    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM="exchange_routing_inform";

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
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //声明一个交换机
            //参数：String exchange, String type
            /**
             * 参数明细：
             * 1、交换机的名称
             * 2、交换机的类型 BuiltinExchangeType
             * fanout：对应的rabbitmq的工作模式是 publish/subscribe
             * direct：对应的Routing	工作模式
             * topic：对应的Topics工作模式
             * headers： 对应的headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            //进行交换机和队列绑定
            //参数：String queue, String exchange, String routingKey
            /**
             * 参数明细：
             * 1、queue 队列名称
             * 2、exchange 交换机名称
             * 3、routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中调协为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_SMS);

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
            //发送邮件消息s
            for (int i = 0; i < 5; i++) {
                String message = "send a message to Email";
                channel.basicPublish(EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_EMAIL,null,message.getBytes());
                System.out.println(" send msg "+message);
            }
            //发送短信消息
            for (int i = 0; i < 5; i++) {
                String message = "send a message to SMS";
                channel.basicPublish(EXCHANGE_ROUTING_INFORM,QUEUE_INFORM_SMS,null,message.getBytes());
                System.out.println(" send msg "+message);
            }
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
