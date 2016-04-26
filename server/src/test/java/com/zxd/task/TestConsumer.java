package com.zxd.task;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zxd on 15/8/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-server-context.xml")
@Transactional
public class TestConsumer {

    @Value("${task.mq.host}")
    private String host;
    @Value("${task.mq.port}")
    private Integer port;
    @Value("${task.mq.name}")
    private String userName;
    @Value("${task.mq.password}")
    private String password;

    private String virtualHost = "zxd_self";

    @Test
    public void test() {
        com.rabbitmq.client.ConnectionFactory rcf = new com.rabbitmq.client.ConnectionFactory();
        rcf.setHost(host);
        rcf.setPort(port);
        rcf.setUsername(userName);
        rcf.setPassword(password);
        rcf.setVirtualHost(virtualHost);

        ConnectionFactory cf = new CachingConnectionFactory(rcf);

        //创建消费消息方法
        Object obj = new Object() {
            public void handleMessage(Object obj) {
                System.out.println("--------------------");
                System.out.println(obj.toString());
            }
        };


        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cf);
        MessageListenerAdapter pushOrderAdapter = new MessageListenerAdapter(obj);
        container.setMessageListener(pushOrderAdapter);
        container.setQueueNames("test");
        container.setConcurrentConsumers(10);
        container.setMaxConcurrentConsumers(20);
        container.start();
    }

    @Test
    public void test1() throws Exception {
        com.rabbitmq.client.ConnectionFactory rcf = new com.rabbitmq.client.ConnectionFactory();
        //设置ip、端口、用户名、密码、虚拟主机
        rcf.setHost(host);
        rcf.setPort(port);
        rcf.setUsername(userName);
        rcf.setPassword(password);
        rcf.setVirtualHost(virtualHost);

        //打开连接和创建通道，与发送端一样
        Connection connection = rcf.newConnection();
        Channel channel = connection.createChannel();

        //声明队列
        channel.queueDeclare("test_share", true, false, false, null);

        //创建队列消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //指定消费队列，自动确认
        channel.basicConsume("test_share", true, consumer);
//        channel.basicConsume("test_share", true, consumer);//手动确认
        while (true) {
            //nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println("----------------------" + message + "'");
//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);//确认消费消息
        }
    }

}
