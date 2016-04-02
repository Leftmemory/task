package com.zxd.task.kafka.consumer;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by zxd on 15/8/5.
 * 卡夫卡consumer
 */
@Slf4j
public class KafkaConsumerFactory {

    @Setter
    private ConsumerConnector connector;
    @Setter
    private String kafkaSwitch;
    @Setter
    private List<KafkaConsumer> processList;

    public void init() {
        System.out.println("----------------kafkaSwitch" + kafkaSwitch);
        if (!"on".equals(kafkaSwitch)) {
            return;
        }
        log.info("KafkaConsumerFactory 开始启动");

        for (KafkaConsumer e : processList) {
            Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.
                    createMessageStreams(e.getTopics());
            List<KafkaStream<byte[], byte[]>> partitions = streams.get(e.getTopic());
            if (partitions != null) {
                e.handleMessage(partitions);
            }
        }

    }

}
