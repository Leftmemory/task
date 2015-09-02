package com.zxd.task.cache;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

/**
 * Created by zxd on 2015/6/15.
 * Jedis客户端。提供缓存基础操作
 */
@Slf4j
public class JedisClient {
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = -1;
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 1000;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    private JedisPool jedisPool = null;

    private String domain;

    private String password;

    private int database;

    private boolean connected = false;

    private static final List<Class> SIMPLE_CLASS_OBJ = Lists.newArrayList();

    static {
        SIMPLE_CLASS_OBJ.add(Number.class);
        SIMPLE_CLASS_OBJ.add(String.class);
        SIMPLE_CLASS_OBJ.add(Boolean.class);
    }

    public JedisClient(String domain, String password) {
        this.domain = domain;
        this.password = password;
        this.database = 0;
        init();
    }

    public JedisClient(String domain, String password, int database) {
        this.domain = domain;
        this.password = password;
        this.database = database;
        init();
    }

    private void init() {
        if (connected) return;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);

        int index = domain.indexOf(':');
        String host = domain.substring(0, index).trim();
        int port = Integer.parseInt(domain.substring(index + 1).trim());
        jedisPool = new JedisPool(config, host, port, 2000, password, database);
        Object testResult = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
                return jedis != null;
            }
        }, true);
        if (testResult == null || testResult.equals(false)) {
            log.error("!!!!!test connect redis server failed: domain: " + domain + "!!!!!!!!!!!!");
            destroy();
        } else {
            log.warn("test connect redis server succeed: domain: " + domain);
            connected = true;
        }
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
        connected = false;
    }

    private Object runTask(Callback callback) {
        return runTask(callback, false);
    }

    private Object runTask(Callback callback, boolean isInit) {
        if (!isInit) {
            init();
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return callback.onTask(jedis);
        } catch (Exception e) {
            log.error("Redis runTask error: ", e);
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            if (!isInit && e.getCause() != null && e.getCause().getMessage().contains("Connection refused")) {
                destroy();
            }
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

    public Object getAllKeys(final String str) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
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

    public <T> List<T> getListRange(final String key) {
        Object ret = runTask(new Callback() {
            @Override
            public Object onTask(Jedis jedis) {
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
