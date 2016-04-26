package com.zxd.task.mq.send;

import lombok.Data;

/**
 * 发送消息的实力
 * 需指定exchange和routingKey
 * Created by hzzhangxiaodan on 2016/4/25.
 */
@Data
public class RabbitMqSenderInfo {
    private String exchange;

    private String routingKey;
}
