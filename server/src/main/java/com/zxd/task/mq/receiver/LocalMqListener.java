package com.zxd.task.mq.receiver;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * @author zxd
 * @since 16/8/3.
 */
@Slf4j
public class LocalMqListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            String str = new String(message.getBody());
            System.out.println("-----------" + str);
        } catch (Exception e) {
            log.error("process error" + message, e);
        }
    }
}
