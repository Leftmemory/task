package com.zxd.task.util;

import com.google.common.collect.Maps;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by zxd on 2015/6/16.
 */
public class ReflectionUtil {

    public static void setValueMap(Object obj, Class cls, Map<String, String> map) {
        Field[] fields = cls.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }
        for (Field f : fields) {
            String varName = f.getName();
            boolean accessFlag = f.isAccessible();
            f.setAccessible(true);
            Object o = ReflectionUtils.getField(f, obj);
            if (o != null) {
                map.put(varName, o.toString());
            }
            f.setAccessible(accessFlag);
        }
        cls = cls.getSuperclass();
        if (cls == null || cls == Object.class) return;
        //递归父类
        ReflectionUtil.setValueMap(obj, cls, map);
    }

    public static String getUrlParam(Object obj) {
        StringBuffer sb = new StringBuffer();
        Class cls = obj.getClass();
        Map<String, String> map = Maps.newHashMap();
        ReflectionUtil.setValueMap(obj, cls, map);
        if (map.size() > 0) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                if (e.getValue() != null && !"".equals(e.getValue())) {
                    sb.append("&").append(e.getKey()).append("=").append(e.getValue());
                }
            }
        }
        return sb.toString();
    }
}
