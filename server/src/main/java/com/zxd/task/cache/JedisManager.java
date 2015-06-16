package com.zxd.task.cache;

import javax.annotation.Resource;

/**
 * Created by zxd on 2015/6/16.
 * jedisClient封装，其他缓存类继承该类
 */
public class JedisManager {

    @Resource
    JedisClient jedisClient;

    public <T> T getHash(final String key, final String field, final Class<T> cls) {
        return jedisClient.getHash(key, field, cls);
    }

    public boolean setHash(final String key, final String field, final Object value, final int seconds) {
        return jedisClient.setHash(key, field, value, seconds);
    }
}
