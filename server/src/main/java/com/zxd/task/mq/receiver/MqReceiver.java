package com.zxd.task.mq.receiver;

import lombok.Getter;
import lombok.Setter;

/**
 * 接收消息
 * Created by hzzhangxiaodan on 2016/4/25.
 */
public abstract class MqReceiver {

    @Getter
    @Setter
    private String queueName;//消费的消息


    public abstract void handleMessage(Object obj);
}
