///**
// * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
// * <p/>
// * Date: 2017年5月8日
// */
//
//package com.zxd.task.cache;
//
//import com.caucho.hessian.io.Hessian2Input;
//import com.caucho.hessian.io.Hessian2Output;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.ListMultimap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.netease.haitao.cache.local.KaolaCache;
//import com.netease.kaola.compose.coupon.common.constant.CacheTime;
//import com.netease.kaola.compose.coupon.common.util.LocalCacheUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.math.NumberUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.redis.core.script.DigestUtils;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.ShardedJedis;
//import redis.clients.jedis.ShardedJedisPipeline;
//import redis.clients.jedis.ShardedJedisSentinelPool;
//import redis.clients.util.SafeEncoder;
//
//import javax.annotation.Resource;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//
///**
// * setinal分配模式
// */
//@Service("shardedRedisClient")
//public class ShardedRedisClient {
//
//    private Logger logger = LoggerFactory.getLogger(ShardedRedisClient.class);
//
//    @Resource(name = "shardedJedisPool")
//    private ShardedJedisSentinelPool shardedJedisPool;
//
//    private ExecutorService es = java.util.concurrent.Executors.newCachedThreadPool();
//
//    private final static Map<String, String> scriptShaMap = Maps.newConcurrentMap();
//
//    private final static byte[] NX_BYTE_ARRAY = SafeEncoder.encode("NX");
//    private final static byte[] EX_BYTE_ARRAY = SafeEncoder.encode("EX");
//
//
//    public interface Callback<V> {
//        V doProcess(ShardedJedis shardedJedis) throws Exception;
//    }
//
//    public <V> V doInRedis(Callback<V> callback) {
//        ShardedJedis shardedJedis = null;
//        try {
//            shardedJedis = shardedJedisPool.getResource();
//            shardedJedis.setDataSource(shardedJedisPool);
//            return callback.doProcess(shardedJedis);
//        } catch (Exception e) {
//            logger.error("redis operation exception.", e);
//        } finally {
//            if (shardedJedis != null) {
//                shardedJedis.close();
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 根据key获取value
//     */
//    public <V> V get(final String key) {
//        return doInRedis(new Callback<V>() {
//            @Override
//            public V doProcess(ShardedJedis shardedJedis) throws Exception {
//                byte[] ret = shardedJedis.get(SafeEncoder.encode(key));
//                return deserialize(ret);
//            }
//        });
//    }
//
//    public <V> List<V> multiGetLocalCache(String prefix, List<String> keyList, KaolaCache localCache) {
//        return LocalCacheUtils.multiGetWithLocalCache(prefix, keyList, new LocalCacheUtils.LocalCacheCallBack<String, V>() {
//            @Override
//            public Map<String, V> apply(List<String> keys) {
//                return multiGet4Map(keys);
//            }
//        }, localCache);
//    }
//
//
//    public <V> Map<String, V> multiGet4Map(final List<String> keys) {
//        final Map<String, V> retMap = Maps.newHashMap();
//        if (CollectionUtils.isEmpty(keys)) {
//            return retMap;
//        }
//
//        final List<String> keyList = Lists.newArrayList(Sets.newHashSet(keys));
//
//        return doInRedis(new Callback<Map<String, V>>() {
//            @Override
//            public Map<String, V> doProcess(ShardedJedis shardedJedis) throws Exception {
//                ListMultimap<Jedis, String> jedisMultimap = ArrayListMultimap.create();
//                for (String key : keyList) {
//                    jedisMultimap.put(shardedJedis.getShard(key), key);
//                }
//
//                List<Callable<Map<String, V>>> taskList = Lists.newArrayList();
//
//                // 获取具体的分片数，去获取数据
//                for (final Jedis jedis : jedisMultimap.keySet()) {
//                    final List<String> shardKeys = jedisMultimap.get(jedis);
//                    final byte[][] shardByteArray = new byte[shardKeys.size()][];
//                    int index = 0;
//                    for (String rawKey : shardKeys) {
//                        shardByteArray[index] = SafeEncoder.encode(rawKey);
//                        index++;
//                    }
//
//                    Callable<Map<String, V>> task = new Callable<Map<String, V>>() {
//                        @Override
//                        public Map<String, V> call() throws Exception {
//                            Map<String, V> shardResult = Maps.newHashMapWithExpectedSize(shardByteArray.length);
//                            List<byte[]> resultList = jedis.mget(shardByteArray);
//                            for (int i = 0; i < resultList.size(); i++) {
//                                V data = deserialize(resultList.get(i));
//                                if (data != null) {
//                                    String key = shardKeys.get(i);
//                                    shardResult.put(key, data);
//                                }
//                            }
//                            return shardResult;
//                        }
//                    };
//                    taskList.add(task);
//                }
//
//                // 并行处理
//                List<Future<Map<String, V>>> futureList = es.invokeAll(taskList);
//                for (Future<Map<String, V>> future : futureList) {
//                    Map<String, V> cacheResult;
//                    try {
//                        cacheResult = future.get();
//                        retMap.putAll(cacheResult);
//                    } catch (Exception e) {
//                        logger.info("get future error", e);
//                    }
//                }
//                return retMap;
//            }
//        });
//    }
//
//    /**
//     * 批量获取
//     */
//    public <V> List<V> multiGet(List<String> keys) {
//        if (CollectionUtils.isEmpty(keys)) {
//            return Lists.newArrayList();
//        }
//
//        Map<String, V> retMap = multiGet4Map(keys);
//        Set<String> filterSet = Sets.newHashSet();
//        List<V> retList = Lists.newArrayList();
//        for (String key : keys) {
//            V obj = retMap.get(key);
//            if (obj != null && !filterSet.contains(key)) {
//                retList.add(obj);
//                filterSet.add(key);
//            }
//        }
//        return retList;
//    }
//
//    public <V> void mset(final Map<String, V> map, final int timeout) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                ShardedJedisPipeline pipeline = shardedJedis.pipelined();
//                for (Map.Entry<String, V> entry : map.entrySet()) {
//                    pipeline.setex(SafeEncoder.encode(entry.getKey()), getRealTimeout(timeout), serialize(entry.getValue()));
//                }
//                pipeline.sync();
//                return null;
//            }
//        });
//    }
//
//    /**
//     * 根据key设置value 过期时间单位:秒
//     */
//    public void set(final String key, final Object value, final int timeout) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                shardedJedis.setex(SafeEncoder.encode(key), getRealTimeout(timeout), serialize(value));
//                return null;
//            }
//        });
//    }
//
//
//    public boolean setnx(final String key, final Object value, final int timeout) {
//        Boolean ret = doInRedis(new Callback<Boolean>() {
//            @Override
//            public Boolean doProcess(ShardedJedis shardedJedis) throws Exception {
//                String result = shardedJedis.set(SafeEncoder.encode(key), serialize(value), NX_BYTE_ARRAY,
//                        EX_BYTE_ARRAY, getRealTimeout(timeout));
//                return StringUtils.isNotBlank(result);
//            }
//        });
//        return Boolean.TRUE.equals(ret);
//    }
//
//
//    /**
//     * 删除指定key的缓存
//     *
//     * @param key
//     * @author wei.zw
//     */
//    public void delete(final String key) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                shardedJedis.del(key);
//                return null;
//            }
//        });
//    }
//
//    /**
//     * 删除指定key的缓存
//     *
//     * @param keyList
//     * @author wei.zw
//     */
//    public void batchDel(final List<String> keyList) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                ShardedJedisPipeline pipeline = shardedJedis.pipelined();
//                for (String key : keyList) {
//                    pipeline.del(key);
//                }
//                pipeline.sync();
//                return null;
//            }
//        });
//    }
//
//
//    public boolean exists(final String key) {
//        Boolean ret = doInRedis(new Callback<Boolean>() {
//            @Override
//            public Boolean doProcess(ShardedJedis shardedJedis) throws Exception {
//                return shardedJedis.exists(key);
//            }
//        });
//        return Boolean.TRUE.equals(ret);
//    }
//
//    public void setCount(final String key, final long count) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                shardedJedis.set(key, String.valueOf(count));
//                return null;
//            }
//        });
//    }
//
//    public Long getCount(final String key) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                String result = shardedJedis.get(key);
//                return result != null ? Long.valueOf(result) : null;
//            }
//        });
//    }
//
//
//    public void setCountHash(final String key, final String hashKey, final long count) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                shardedJedis.hset(key, hashKey, String.valueOf(count));
//                return null;
//            }
//        });
//    }
//
//    public Long getCountHash(final String key, final String hashKey) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                String result = shardedJedis.hget(key, hashKey);
//                return NumberUtils.toLong(result);
//            }
//        });
//    }
//
//
//    public Map<String, Long> batchGetCountHash(final String key, final List<String> hashKeyList) {
//        return doInRedis(new Callback<Map<String, Long>>() {
//            @Override
//            public Map<String, Long> doProcess(ShardedJedis shardedJedis) throws Exception {
//                List<String> result = shardedJedis.hmget(key, hashKeyList.toArray(new String[0]));
//                Map<String, Long> map = Maps.newHashMap();
//                for (int i = 0; i < hashKeyList.size(); i++) {
//                    map.put(hashKeyList.get(i), NumberUtils.toLong(result.get(i)));
//                }
//                return map;
//            }
//        });
//    }
//
//    public Long incr(final String key, final long step) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                return shardedJedis.incrBy(key, step);
//            }
//        });
//    }
//
//    public Long incrHash(final String key, final String hashKey, final long step) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                return shardedJedis.hincrBy(key, hashKey, step);
//            }
//        });
//    }
//
//    public void batchIncrHash(final String key, final Map<String, Long> hashValueMap, final int timeout) {
//        doInRedis(new Callback<Void>() {
//            @Override
//            public Void doProcess(ShardedJedis shardedJedis) throws Exception {
//                ShardedJedisPipeline pipeline = shardedJedis.pipelined();
//                for (Map.Entry<String, Long> entry : hashValueMap.entrySet()) {
//                    pipeline.hincrBy(key, entry.getKey(), entry.getValue());
//                }
//                pipeline.expire(key, getRealTimeout(timeout));
//
//                pipeline.sync();
//                return null;
//            }
//        });
//    }
//
//    public Long expire(final String key, final int timeout) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                return shardedJedis.expire(key, getRealTimeout(timeout));
//            }
//        });
//    }
//
//
//    public <V> Long sadd(final String key, final List<V> values) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                byte[][] valueBytes = new byte[values.size()][];
//                int index = 0;
//                for (V value : values) {
//                    valueBytes[index] = serialize(value);
//                    index++;
//                }
//                return shardedJedis.sadd(SafeEncoder.encode(key), valueBytes);
//            }
//        });
//    }
//
//    public <V> Long srem(final String key, final List<V> values) {
//        return doInRedis(new Callback<Long>() {
//            @Override
//            public Long doProcess(ShardedJedis shardedJedis) throws Exception {
//                byte[][] valueBytes = new byte[values.size()][];
//                int index = 0;
//                for (V value : values) {
//                    valueBytes[index] = serialize(value);
//                    index++;
//                }
//                return shardedJedis.srem(SafeEncoder.encode(key), valueBytes);
//            }
//        });
//    }
//
//    public <V> Set<V> smembers(final String key) {
//        return doInRedis(new Callback<Set<V>>() {
//            @Override
//            public Set<V> doProcess(ShardedJedis shardedJedis) throws Exception {
//                Set<V> set = Sets.newHashSet();
//                Set<byte[]> results = shardedJedis.smembers(SafeEncoder.encode(key));
//                if (CollectionUtils.isEmpty(results)) {
//                    return set;
//                }
//                for (byte[] result : results) {
//                    set.add((V) deserialize(result));
//                }
//                return set;
//            }
//        });
//    }
//
//
//    public Object executeByLua(final String script, final String key, final List<String> args) {
//        return doInRedis(new Callback<Object>() {
//            @Override
//            public Object doProcess(ShardedJedis shardedJedis) throws Exception {
//                Jedis jedis = shardedJedis.getShard(key);
//                Object result;
//                try {
//                    result = jedis.evalsha(getScriptSha(script), Lists.newArrayList(key), args);
//                } catch (Exception e) {
//                    if (!exceptionContainsNoScriptError(e)) {
//                        throw new RuntimeException(e.getMessage(), e);
//                    }
//                    result = jedis.eval(script, Lists.newArrayList(key), args);
//                }
//                return result;
//            }
//        });
//    }
//
//    private String getScriptSha(String script) {
//        String sha = scriptShaMap.get(script);
//
//        if (StringUtils.isNotBlank(sha)) {
//            return sha;
//        }
//        sha = DigestUtils.sha1DigestAsHex(script);
//        scriptShaMap.put(script, sha);
//        return sha;
//    }
//
//
//    private boolean exceptionContainsNoScriptError(Exception e) {
//        Throwable current = e;
//        while (current != null) {
//
//            String exMessage = current.getMessage();
//            if (exMessage != null && exMessage.contains("NOSCRIPT")) {
//                return true;
//            }
//
//            current = current.getCause();
//        }
//
//        return false;
//    }
//
//    private byte[] serialize(Object obj) {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        Hessian2Output ho = new Hessian2Output(os);
//        try {
//            ho.writeObject(obj);
//            ho.close();
//            return os.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage(), e.getCause());
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private <V> V deserialize(byte[] bytes) {
//        if (bytes == null) {
//            return null;
//        }
//        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
//        Hessian2Input hi = new Hessian2Input(is);
//        try {
//            return (V) hi.readObject();
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage(), e.getCause());
//        }
//    }
//
//
//    private int getRealTimeout(int timeout) {
//        return timeout <= 0 ? CacheTime.CACHE_EXP_FOREVER : timeout;
//    }
//}
