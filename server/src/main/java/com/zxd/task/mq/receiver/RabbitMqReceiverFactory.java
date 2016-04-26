package com.zxd.task.mq.receiver;

import lombok.Setter;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import java.util.List;

/**
 * 接收消息监听初始类
 * Created by hzzhangxiaodan on 2016/4/22.
 */
public class RabbitMqReceiverFactory {

    @Setter
    private CachingConnectionFactory cachingConnectionFactory;

    @Setter
    private List<MqReceiver> processList;

    public void init(){
        for (MqReceiver e : processList) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cachingConnectionFactory);
            MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(e);
            container.setMessageListener(messageListenerAdapter);
            container.setQueueNames(e.getQueueName());
            container.setConcurrentConsumers(10);
            container.setMaxConcurrentConsumers(20);
            container.start();
        }
    }
}
