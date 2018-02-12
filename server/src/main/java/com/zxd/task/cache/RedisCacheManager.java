package com.zxd.task.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 封装构建缓存
 *
 * @author zxd
 * @since 17/3/9.
 */
@Component
@Slf4j
public class RedisCacheManager {

//    @Autowired
//    private RedisClient redisClient;
//
//    private final static int defaultInvalidExpire = 10;//无效结果默认缓存时间
//
//
//    /**
//     * 业务回调
//     *
//     * @param <V>
//     */
//    public interface CacheProcessForObj<V> {
//        V doProcess();
//    }
//
//    /**
//     * 业务回调
//     *
//     * @param <V>
//     */
//    public interface CacheProcessForHash<T, V> {
//        Map<T, V> doProcess();
//    }
//
//    /**
//     * 业务回调, 带参数,补全用
//     *
//     * @param <V>
//     */
//    public interface CacheProcessForHash2<T, V> {
//        Map<T, V> doProcess(List<T> hashKeyList);
//    }
//
//
//    /**
//     * get 封装处理
//     *
//     * @param cacheKey   缓存key
//     * @param isCover    是否强制覆盖
//     * @param expireTime 缓存时间
//     * @param process    回调
//     */
//    @SuppressWarnings("unchecked")
//    public <V> V get(String cacheKey,
//                     boolean isCover,
//                     int expireTime,
//                     CacheProcessForObj<V> process) {
//        if (!isCover) {
//            CacheWrapper cacheWrapper = redisClient.get(cacheKey);
//            if (cacheWrapper != null) {
//                return (V) cacheWrapper.getData();
//            }
//        }
//
//        Object obj = process.doProcess();
//
//        buildCache(cacheKey, expireTime, obj);
//        return (V) obj;
//    }
//
//    /**
//     * getHash 封装处理
//     *
//     * @param cacheKey   缓存key
//     * @param hashKey    hashKey
//     * @param isCover    是否强制覆盖
//     * @param expireTime 缓存时间
//     * @param process    回调
//     */
//    @SuppressWarnings("unchecked")
//    public <T, V> V getHash(String cacheKey,
//                            T hashKey,
//                            boolean isCover,
//                            int expireTime,
//                            CacheProcessForHash<T, V> process) {
//        if (!isCover) {
//            CacheWrapper cacheWrapper = redisClient.getHash(cacheKey, hashKey);
//            if (cacheWrapper != null) {
//                return (V) cacheWrapper.getData();
//            }
//        }
//
//        Map<T, V> map = process.doProcess();
//        Object obj = null;
//        if (MapUtils.isNotEmpty(map)) {
//            obj = map.get(hashKey);
//        }
//        buildCacheHash(cacheKey, hashKey, expireTime, obj);
//        return (V) obj;
//    }
//
//    /**
//     * getHashMulti 封装处理，批量获取hash
//     *
//     * @param key         缓存key
//     * @param hashKeyList hashKey列表
//     * @param isCover     是否强制覆盖
//     * @param expireTime  过期时间
//     * @param process     存在空值时，需要走db查询的具体实现
//     * @param <T>         hashKey类型
//     * @param <V>         返回接过类型
//     */
//    @SuppressWarnings("unchecked")
//    public <T, V> List<V> getHashMultiWithoutNull(String key,
//                                                  List<T> hashKeyList,
//                                                  boolean isCover,
//                                                  int expireTime,
//                                                  CacheProcessForHash2<T, V> process) {
//        if (CollectionUtils.isEmpty(hashKeyList)) {
//            return Lists.newArrayList();
//        }
//        Map<T, V> resultMap = Maps.newLinkedHashMap();
//        List<T> noCachedKeyList = Lists.newArrayList();
//        if (!isCover) {
//            List<CacheWrapper> cacheWrapperList = redisClient.getHashMulti(key, hashKeyList);
//            //缓存存在
//            if (CollectionUtils.isNotEmpty(cacheWrapperList)) {
//                int size = cacheWrapperList.size();
//                if (size != hashKeyList.size()) {
//                    log.error("key : " + key + " hashKeyList : " + FastJsonUtil.toJSONString(hashKeyList)
//                            + "查询异常，返回结果记录数不匹配");
//                    return Lists.newArrayList();
//                }
//                for (int i = 0; i < size; i++) {
//                    CacheWrapper cacheWrapper = cacheWrapperList.get(i);
//                    if (cacheWrapper == null) {
//                        noCachedKeyList.add(hashKeyList.get(i));
//                        continue;
//                    }
//                    if (isValidResult(cacheWrapper.getData())) {
//                        resultMap.put(hashKeyList.get(i), (V) cacheWrapper.getData());
//                    }
//                }
//            } else {
//                noCachedKeyList.addAll(hashKeyList);
//            }
//        } else {
//            noCachedKeyList.addAll(hashKeyList);
//        }
//        if (CollectionUtils.isEmpty(noCachedKeyList)) {
//            return Lists.newArrayList(resultMap.values());
//        }
//
//        Map<T, V> map = process.doProcess(noCachedKeyList);
//        if (MapUtils.isEmpty(map)) {
//            return Lists.newArrayList(resultMap.values());
//        }
//        buildCacheHash(key, map, expireTime);
//
//        List<V> retList = Lists.newArrayList();
//        resultMap.putAll(map);
//        for (T hashKey : hashKeyList) {
//            V obj = resultMap.get(hashKey);
//            if (obj != null) {
//                retList.add(obj);
//            }
//        }
//        return retList;
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T, V> Map<T, V> getHashMultiMapWithoutNull(String key,
//                                                       List<T> hashKeyList,
//                                                       boolean isCover,
//                                                       int expireTime,
//                                                       CacheProcessForHash2<T, V> process) {
//        if (CollectionUtils.isEmpty(hashKeyList)) {
//            return Maps.newHashMap();
//        }
//        Map<T, V> resultMap = Maps.newLinkedHashMap();
//        List<T> noCachedKeyList = Lists.newArrayList();
//        if (!isCover) {
//            List<CacheWrapper> cacheWrapperList = redisClient.getHashMulti(key, hashKeyList);
//            //缓存存在
//            if (CollectionUtils.isNotEmpty(cacheWrapperList)) {
//                int size = cacheWrapperList.size();
//                if (size != hashKeyList.size()) {
//                    log.error("key : " + key + " hashKeyList : " + FastJsonUtil.toJSONString(hashKeyList)
//                            + "查询异常，返回结果记录数不匹配");
//                    return Maps.newHashMap();
//                }
//                for (int i = 0; i < size; i++) {
//                    CacheWrapper cacheWrapper = cacheWrapperList.get(i);
//                    if (cacheWrapper == null) {
//                        noCachedKeyList.add(hashKeyList.get(i));
//                        continue;
//                    }
//                    if (isValidResult(cacheWrapper.getData())) {
//                        resultMap.put(hashKeyList.get(i), (V) cacheWrapper.getData());
//                    }
//                }
//            } else {
//                noCachedKeyList.addAll(hashKeyList);
//            }
//        } else {
//            noCachedKeyList.addAll(hashKeyList);
//        }
//        if (CollectionUtils.isEmpty(noCachedKeyList)) {
//            return resultMap;
//        }
//
//        Map<T, V> map = process.doProcess(noCachedKeyList);
//        if (MapUtils.isEmpty(map)) {
//            return resultMap;
//        }
//        buildCacheHash(key, map, expireTime);
//
//        List<V> retList = Lists.newArrayList();
//        resultMap.putAll(map);
//        return map;
//    }
//
//    private void buildCache(String cacheKey, int expireTime, Object obj) {
//        //结果有效性校验,有效设置正常缓存时间，否则，设置短期缓存
//        if (isValidResult(obj)) {
//            CacheWrapper cacheWrapper = CacheWrapper.wrapValid(obj, System.currentTimeMillis(),
//                    expireTime, 1);
//            redisClient.set(cacheKey, cacheWrapper, expireTime);
//        } else {
//            CacheWrapper cacheWrapper = CacheWrapper.wrapValid(obj, System.currentTimeMillis(),
//                    defaultInvalidExpire, 1);
//            redisClient.set(cacheKey, cacheWrapper, defaultInvalidExpire);
//        }
//    }
//
//
//    private <T> void buildCacheHash(String cacheKey, T hashKey, int expireTime, Object obj) {
//        //结果有效性校验,有效设置正常缓存时间，否则，设置短期缓存
//        if (isValidResult(obj)) {
//            CacheWrapper cacheWrapper = CacheWrapper.wrapValid(obj, System.currentTimeMillis(),
//                    expireTime, 1);
//            redisClient.setHash(cacheKey, hashKey, cacheWrapper, expireTime);
//        } else {
//            CacheWrapper cacheWrapper = CacheWrapper.wrapValid(obj, System.currentTimeMillis(),
//                    defaultInvalidExpire, 1);
//            redisClient.setHash(cacheKey, hashKey, cacheWrapper, defaultInvalidExpire);
//        }
//    }
//
//    private <T, V> void buildCacheHash(String cacheKey, Map<T, V> hashObject, int expireTime) {
//        //结果有效性校验,有效设置正常缓存时间，否则，设置短期缓存
//        Map<T, Object> objectHashMap = Maps.newHashMap();
//        for (Map.Entry<T, V> entry : hashObject.entrySet()) {
//            Object obj = entry.getValue();
//            CacheWrapper cacheWrapper;
//            if (isValidResult(entry.getValue())) {
//                cacheWrapper = CacheWrapper.wrapValid(obj, System.currentTimeMillis(),
//                        expireTime, 1);
//            } else {
//                cacheWrapper = CacheWrapper.wrapValid(obj, System.currentTimeMillis(),
//                        defaultInvalidExpire, 1);
//            }
//            objectHashMap.put(entry.getKey(), cacheWrapper);
//        }
//        redisClient.setMultiHash(cacheKey, objectHashMap, expireTime);
//    }
//
//
//    private boolean isValidResult(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//
//        //map 集合处理
//        if (obj instanceof Map && MapUtils.isEmpty((Map) obj)) {
//            return false;
//        }
//        //set或list 接好
//        if (obj instanceof Collection && CollectionUtils.isEmpty((Collection) obj)) {
//            return false;
//        }
//        return true;
//    }
}
