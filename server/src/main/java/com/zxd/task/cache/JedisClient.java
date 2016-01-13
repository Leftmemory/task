package com.zxd.task.cache;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.SafeEncoder;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zxd on 2015/6/15.
 * Jedis客户端。提供缓存基础操作
 */
@Slf4j
public class JedisClient {
    /**
     * 缓存时效 1分钟
     */
    public static int CACHE_EXP_MINUTE = 60;

    /**
     * 缓存时效 10分钟
     */
    public static int CACHE_EXP_MINUTES = 60 * 10;

    /**
     * 缓存时效 60分钟
     */
    public static int CACHE_EXP_HOURS = 60 * 60;

    /**
     * 缓存时效 1天
     */
    public static int CACHE_EXP_DAY = 3600 * 24;

    /**
     * 缓存时效 1周
     */
    public static int CACHE_EXP_WEEK = 3600 * 24 * 7;

    /**
     * 缓存时效 1月
     */
    public static int CACHE_EXP_MONTH = 3600 * 24 * 30 * 7;

    /**
     * 缓存时效 永久
     */
    public static int CACHE_EXP_FOREVER = 0;

    @Autowired
    private JedisSentinelPool jedisPool;

    private static final List<Class> SIMPLE_CLASS_OBJ = Lists.newArrayList();

    static {
        SIMPLE_CLASS_OBJ.add(Number.class);
        SIMPLE_CLASS_OBJ.add(String.class);
        SIMPLE_CLASS_OBJ.add(Boolean.class);
    }

    /**
     * 获取jedis实例
     */
    public Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis jedis = jedisPool.getResource();
                return jedis;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void destroy() {
        jedisPool.destroy();
    }

    private Object runTask(Callback callback) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return callback.onTask(jedis);
        } catch (Exception e) {
            log.error("Redis runTask error: ", e);
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return null;
    }

    public <T> T getHash(final String key, final String field, final Class<T> cls) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                Object obj = null;
                if (isSimpleObj(cls)) {
                    String str = jedis.hget(key, field);
                    if (str != null)
                        obj = createSimpleObj(str, cls);
                } else {
                    byte[] bs = jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
                    if (bs != null) {
                        obj = deserialize(bs);
                    }
                }
                return obj;
            }
        });
        return ret == null ? null : (T) ret;
    }

    public <T> T getHash(final int dbIndex, final String key, final String field, final Class<T> cls) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                jedis.select(dbIndex);
                Object obj = null;
                if (isSimpleObj(cls)) {
                    String str = jedis.hget(key, field);
                    if (str != null)
                        obj = createSimpleObj(str, cls);
                } else {
                    byte[] bs = jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
                    if (bs != null) {
                        obj = deserialize(bs);
                    }
                }
                return obj;
            }
        });
        return ret == null ? null : (T) ret;
    }

    public boolean setHash(final String key, final String field, final Object value, final int seconds) {
        if (key == null || field == null || value == null) {
            return false;
        }
        final Object success = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                Long ret;
                if (isSimpleObj(value.getClass())) {
                    ret = jedis.hset(key, field, value.toString());
                } else {
                    byte[] bKey = SafeEncoder.encode(key);
                    byte[] bField = SafeEncoder.encode(field);
                    byte[] bValue = serialize(value);
                    ret = jedis.hset(bKey, bField, bValue);
                }
                if (seconds != 0) {
                    jedis.expire(key, seconds);
                }
                return ret != null && ret == 1;
            }
        });
        return success != null && (boolean) success;
    }

    public boolean setHash(final int dbIndex, final String key, final String field, final Object value, final int seconds) {
        if (key == null || field == null || value == null) {
            return false;
        }
        final Object success = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                Long ret;
                jedis.select(dbIndex);
                if (isSimpleObj(value.getClass())) {
                    ret = jedis.hset(key, field, value.toString());
                } else {
                    byte[] bKey = SafeEncoder.encode(key);
                    byte[] bField = SafeEncoder.encode(field);
                    byte[] bValue = serialize(value);
                    ret = jedis.hset(bKey, bField, bValue);
                }
                if (seconds != 0) {
                    jedis.expire(key, seconds);
                }
                return ret != null && ret == 1;
            }
        });
        return success != null && (boolean) success;
    }


    public Object getAllKeys(final String str) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                return jedis.keys(str);
            }
        });
        return ret;
    }

    public Object getAllKeys(final int dbIndex, final String str) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                jedis.select(dbIndex);
                return jedis.keys(str);
            }
        });
        return ret;
    }

    public Object getAllHkeys(final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
        return ret;
    }

    public Object getAllHkeys(final int dbIndex, final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                jedis.select(dbIndex);
                return jedis.hkeys(key);
            }
        });
        return ret;
    }

    public <K, V> Map<K, V> getAllHash(final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
        return ret == null ? Collections.<K, V>emptyMap() : (Map<K, V>) ret;
    }

    public <K, V> Map<K, V> getAllHash(final int dbIndex, final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                jedis.select(dbIndex);
                return jedis.hgetAll(key);
            }
        });
        return ret == null ? Collections.<K, V>emptyMap() : (Map<K, V>) ret;
    }

    public <T> List<T> getListRange(final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                return jedis.lrange(key, 0, jedis.llen(key) - 1);
            }
        });
        return ret == null ? Collections.<T>emptyList() : (List<T>) ret;
    }

    public <T> List<T> getListRange(final int dbIndex, final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                jedis.select(dbIndex);
                return jedis.lrange(key, 0, jedis.llen(key) - 1);
            }
        });
        return ret == null ? Collections.<T>emptyList() : (List<T>) ret;
    }


    private static boolean isSimpleObj(Class classObj) {
        for (Class c : SIMPLE_CLASS_OBJ) {
            if (c.isAssignableFrom(classObj))
                return true;
        }
        return false;
    }

    private <T> T createSimpleObj(String arg, Class<T> cls) {
        T ret = null;
        Constructor[] constructors = cls.getDeclaredConstructors();
        for (Constructor c : constructors) {
            Class[] parameterTypes = c.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].equals(String.class)) {
                try {
                    ret = (T) c.newInstance(arg);
                } catch (Exception e) {
                    log.error("create simple obj error: " + e.getMessage(), e);
                }
                break;
            }
        }
        return ret;
    }

    //序列化
    private byte[] serialize(Object object) {
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("设定缓存的对象：" + object.getClass() + "无法序列化，确保 implements Serializable");
        }
        ObjectOutputStream objOS = null;
        ByteArrayOutputStream byteAOS = new ByteArrayOutputStream();
        try {
            objOS = new ObjectOutputStream(byteAOS);
            objOS.writeObject(object);
            return byteAOS.toByteArray();
        } catch (Exception e) {
            log.error("serialize error: " + e.getMessage());
        } finally {
            try {
                if (objOS != null) objOS.close();
                byteAOS.close();
            } catch (IOException e) {
                log.error("serialize close error : " + e.getMessage());
            }
        }
        return null;
    }

    //反序列化
    private Object deserialize(byte[] bytes) {
        ByteArrayInputStream byteAIS = new ByteArrayInputStream(bytes);
        ObjectInputStream objIS = null;
        try {
            objIS = new ObjectInputStream(byteAIS);
            return objIS.readObject();
        } catch (Exception e) {
            log.error("deserialize error: " + e.getMessage());
        } finally {
            try {
                byteAIS.close();
                if (objIS != null) objIS.close();
            } catch (IOException e) {
                log.error("deserialize close error: " + e.getMessage());
            }
        }
        return null;
    }

    interface Callback {
        Object onTask(Jedis jedis);
    }
}
