package com.zxd.task.kafka.producer;

import com.zxd.task.util.JsonUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;


/**
 * Created by zxd on 15/8/4.
 * 卡夫卡producer
 */
@Slf4j
public class KafkaProducerFactory {

    @Setter
    private Producer<String, String> producer;


    public boolean doSend(String topicName, Object obj) {
        String message;
        if (obj instanceof String) {
            message = (String) obj;
        } else {
            message = JsonUtil.obj2Str(obj);
        }
        if (message == null) {
            log.error("发送消息, topicName: " + topicName + "发送的消息内容为空或存在异常，无法转换为json字符串");
            return false;
        }
        try {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topicName, message);
            producer.send(producerRecord);
            return true;
        } catch (Exception e) {
            log.error("send notify failed! body" + message, e);
            return false;
        } finally {
            producer.close();
        }
    }

}
