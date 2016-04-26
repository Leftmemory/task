package com.zxd.task.mq.receiver;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * 消息处理
 */
@Slf4j
public class LocalMqReceiver extends MqReceiver {

    @Override
    public void handleMessage(Object obj) {
        if (obj == null || !(obj instanceof String)) {
            log.error("not process", obj);
            return;
        }
        log.info("##=======start process local message:" + obj);
        String str = (String) obj;
        try {
            JSONObject jo = new JSONObject(str);
            String testData = jo.getString("test_data");
            System.out.println("-----------" + testData);
        } catch (Exception e) {
            log.error("process error" + str, e);
        }
    }

}
