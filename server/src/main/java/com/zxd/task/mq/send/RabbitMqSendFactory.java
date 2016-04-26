package com.zxd.task.mq.send;

import com.zxd.task.util.JsonUtil;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

/**
 * rabbitMq发送工厂类
 * Created by hzzhangxiaodan on 2016/4/22.
 */
@Slf4j
public class RabbitMqSendFactory {
    @Setter
    private RabbitTemplate rabbitTemplate;
    @Setter
    private Map<String, RabbitMqSenderInfo> senderMap;


    public boolean doSend(String senderName, Object obj) {
        RabbitMqSenderInfo sender = senderMap.get(senderName);
        if(sender == null){
            log.error("发送消息，senderName：" + senderName + "没有配置");
            return false;
        }
        String message;
        if (obj instanceof String) {
            message = (String) obj;
        } else {
            message = JsonUtil.obj2Str(obj);
        }
        if (message == null) {
            log.error("发送消息, senderName: " + senderName + "发送的消息内容为空或存在异常，无法转换为json字符串");
            return false;
        }
        try {
            rabbitTemplate.convertAndSend(sender.getRoutingKey(), sender.getRoutingKey(), message);
            return true;
        } catch (Exception e) {
            log.error("send notify failed! message" + message, e);
            return false;
        }
    }
}
