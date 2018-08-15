package com.zxd.task.cache.manager;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/10/10.
 */
public abstract class AbstractCacheManager {


    /**
     * 单个key查询
     * @param prefix 前缀或hash结构的大key
     * @param param 原始参数
     * @param timeout 过期时间
     */
    public <T, V> V get(String prefix, T param, int timeout, Supplier<V> supplier) {
        String cacheKey = generateKey(prefix, param);
        //从缓存获取
        V result = getFromCacheSingle(prefix, cacheKey);
        if (result != null) {
            return result;
        }
        //缓存没有，走业务逻辑查询
        result = supplier.get();
        if (result != null) {
            //设置缓存
            setCacheSingle(prefix, cacheKey, result, timeout);
        }
        return result;
    }


    /**
     *  批量查询封装
     * @param prefix 前缀或hash结构的大key
     * @param params 原始参数
     * @param timeout 过期时间
     */
    public <T, V> List<V> multiGet(String prefix, List<T> params, int timeout, CallBack4Map<T, V>
            callBack) {
        if (CollectionUtils.isEmpty(params)) {
            return Lists.newArrayList();
        }
        Map<String, T> tempKeyMap = Maps.newHashMap();
        for (T key : params) {
            tempKeyMap.put(generateKey(prefix, key), key);
        }
        //从缓存获取-批量
        Map<String, V> cacheResultMap = getFromCache(prefix, Lists.<String>newArrayList(tempKeyMap.keySet()));

        //缓存获取不完全，剩余部分走业务逻辑查询
        if (cacheResultMap.size() != tempKeyMap.size()) {
            //过滤缓存没查到参数（原始参数）
            List<T> leftParams = Lists.newArrayList();
            for (Map.Entry<String, T> entry : tempKeyMap.entrySet()) {
                if (cacheResultMap.containsKey(entry.getKey())) {
                    continue;
                }
                leftParams.add(entry.getValue());
            }

            Map<T, V> leftResultMap = callBack.apply(leftParams);

            Map<String, V> leftCacheResultMap = Maps.newHashMap();

            for (Map.Entry<T, V> entry : leftResultMap.entrySet()) {
                leftCacheResultMap.put(generateKey(prefix, entry.getKey()), entry.getValue());
            }

            //剩余部分写缓存
            setCache(prefix, leftCacheResultMap, timeout);
            //汇总
            cacheResultMap.putAll(leftCacheResultMap);
        }

        List<V> resultList = Lists.newArrayList();
        if (MapUtils.isEmpty(cacheResultMap)) {
            return resultList;
        }

        Set<T> filterSet = Sets.newHashSet();
        //汇总本地缓存数据
        for (T key : params) {
            V v = cacheResultMap.get(generateKey(prefix, key));
            if (v != null && !filterSet.contains(key)) {
                resultList.add(v);
                filterSet.add(key);
            }
        }
        return resultList;
    }

    public interface CallBack4Map<T, V> {
        Map<T, V> apply(List<T> keys);
    }

    /**
     * 查询缓存-批量
     *
     * @param prefix 前缀或hash结构的大key
     * @param keys   转置后的key
     */
    public abstract <V> Map<String, V> getFromCache(String prefix, List<String> keys);

    /**
     * 设置缓存-批量
     *
     * @param prefix   前缀或hash结构的大key
     * @param cacheMap key-转置后的key； value-缓存值
     * @param timeout  过期时间，秒
     */
    public abstract <V> void setCache(String prefix, Map<String, V> cacheMap, int timeout);

    /**
     * 查询缓存
     *
     * @param prefix 前缀或hash结构的大key
     * @param key    转置后的key
     */
    public abstract <V> V getFromCacheSingle(String prefix, String key);

    /**
     * @param prefix  前缀或hash结构的大key
     * @param key     转置后的key
     * @param value
     * @param timeout 过期时间，秒
     */
    public abstract <V> void setCacheSingle(String prefix, String key, V value, int timeout);

    /**
     *
     * @param prefix 前缀或hash结构的大key
     * @param key 原始参数
     * @return  根据数据结构构造的缓存key
     */
    public abstract  <T> String generateKey(String prefix, T key);
}
