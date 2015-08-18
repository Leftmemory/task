package com.zxd.task.kafka.consumer;

import kafka.consumer.KafkaStream;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by zxd on 15/8/5.
 */
public abstract class KafkaConsumer {

    @Getter
    @Setter
    private String topic;

    @Getter
    @Setter
    private Map<String, Integer> topics;

    public abstract void handleMessage(List<KafkaStream<byte[], byte[]>> partitions);
}
