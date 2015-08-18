package com.zxd.task.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zxd on 15/8/17.
 */
@Slf4j
public class JsonUtil {

    public static String obj2Str(Object o) {

        try {
            Gson gson = new Gson();
            return gson.toJson(o);
        } catch (Exception e) {
            log.error("反序列化json失败", e);
        }
        return null;
    }
}
