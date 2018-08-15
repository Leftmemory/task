//package com.zxd.task.cache.manager;
//
//import com.zxd.task.cache.ShardedRedisClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author zxd <hzzhangxiaodan@corp.netease.com>
// * @since 17/10/10.
// */
//@Component
//public class ValueOperationManager extends AbstractCacheManager {
//
//    @Autowired
//    private ShardedRedisClient shardedRedisClient;
//
//    @Override
//    public <V> Map<String, V> getFromCache(String prefix, List<String> keys) {
//        return shardedRedisClient.multiGet4Map(keys);
//    }
//
//    @Override
//    public <V> void setCache(String prefix, Map<String, V> cacheMap, int timeout) {
//        shardedRedisClient.mset(cacheMap, timeout);
//    }
//
//    @Override
//    public <V> V getFromCacheSingle(String prefix, String key) {
//        return shardedRedisClient.get(key);
//    }
//
//    @Override
//    public <V> void setCacheSingle(String prefix, String key, V value, int timeout) {
//        shardedRedisClient.set(key, value, timeout);
//    }
//
//    @Override
//    public <T> String generateKey(String prefix, T key) {
//        return prefix + String.valueOf(key);
//    }
//}
