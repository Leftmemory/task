package com.zxd.task.cache.manager;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/10/10.
 */
public class LocalCacheManager extends AbstractCacheManager {


    private static Cache<String, Object> loadingCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(10,
            TimeUnit.MINUTES).build();


    /**
     * 单个key查询 固定前缀
     *
     * @param param 原始参数
     */
    public <T, V> V get(T param, Supplier<V> supplier) {
        return get("COMMON_LOCAL_CACHE__PREFIX_", param, 10, supplier);
    }

    /**
     * 单个key查询
     *
     * @param prefix 前缀
     * @param param  原始参数
     */
    public <T, V> V get(String prefix, T param, Supplier<V> supplier) {
        return get(prefix, param, 10, supplier);
    }

    /**
     * 批量查询封装
     *
     * @param prefix 前缀或hash结构的大key
     * @param params 原始参数
     */
    public <T, V> List<V> multiGet(String prefix, List<T> params, CallBack4Map<T, V> callBack) {
        return multiGet(prefix, params, 10, callBack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Map<String, V> getFromCache(String prefix, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return Maps.newHashMap();
        }
        Map<String, V> map = Maps.newHashMap();
        for (String key : keys) {
            V v = (V) loadingCache.getIfPresent(prefix + key);
            if (v != null) {
                map.put(key, v);
            }
        }
        return map;
    }

    @Override
    public <V> void setCache(String prefix, Map<String, V> cacheMap, int timeout) {
        for (Map.Entry<String, V> entry : cacheMap.entrySet()) {
            if (entry.getValue() != null) {
                loadingCache.put(prefix + entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getFromCacheSingle(String prefix, String key) {
        return (V) loadingCache.getIfPresent(prefix + key);
    }

    @Override
    public <V> void setCacheSingle(String prefix, String key, V value, int timeout) {
        loadingCache.put(prefix + key, value);
    }

    @Override
    public <T> String generateKey(String prefix, T key) {
        return String.valueOf(key);
    }
}
