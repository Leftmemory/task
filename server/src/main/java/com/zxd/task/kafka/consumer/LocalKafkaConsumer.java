package com.zxd.task.kafka.consumer;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zxd on 15/8/5.
 */
public class LocalKafkaConsumer extends KafkaConsumer {
    @Override
    public void handleMessage(List<KafkaStream<byte[], byte[]>> partitions) {
        System.out.println(getTopics());
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (final KafkaStream<byte[], byte[]> partition : partitions) {

            executor.submit(new Runnable() {
                @Override
                public void run() {
                    ConsumerIterator<byte[], byte[]> it = partition.iterator();
                    while (it.hasNext()) {
                        MessageAndMetadata<byte[], byte[]> item = it.next();
                        System.out.println("partition:" + item.partition());
                        System.out.println("offset:" + item.offset());
                        System.out.println(new String(item.message()));
                        System.out.println("---");
                    }
                }
            });
        }
    }
}
