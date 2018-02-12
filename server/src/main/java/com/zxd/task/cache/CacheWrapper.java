package com.zxd.task.cache;

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * 缓存包装类
 *
 * @author zxd
 * @since 16/12/2.
 */
public class CacheWrapper<D> implements Serializable {
    private static final long serialVersionUID = 7814700753027244683L;

    private boolean valid;//是否正常数据

    private D data;//具体数据

    private long createTime;//创建时间

    private long expireTime;//失效时间

    private int rebuildCount = 1;//重建次数，第一次构建为1

    private static Long defaultCacheTime = 86400L;
    /**
     * 构建有效缓存对象
     *
     * @param data 缓存结果
     * @return
     */
    public static <D> CacheWrapper<D> wrapValid(D data, long createTime, long expireTime, int rebuildCount) {
        CacheWrapper<D> cacheWrapper = new CacheWrapper<>();
        cacheWrapper.setCreateTime(createTime);
        //缓存是按秒来的，这里做下转换
        if(expireTime < 0) {
            expireTime = defaultCacheTime;
        }
        cacheWrapper.setExpireTime(expireTime * 1000L);
        cacheWrapper.setRebuildCount(rebuildCount);
        cacheWrapper.setData(data);
        return cacheWrapper;
    }

    /**
     * 构建无效缓存对象
     *
     * @param data 缓存结果
     * @return
     */
    public static <D> CacheWrapper<D> wrapInValid(D data, long createTime, long expireTime, int rebuildCount) {
        CacheWrapper<D> cacheWrapper = new CacheWrapper<>();
        cacheWrapper.setValid(false);
        cacheWrapper.setCreateTime(createTime);
        //缓存是按秒来的，这里做下转换
        if(expireTime < 0) {
            expireTime = defaultCacheTime;
        }
        cacheWrapper.setExpireTime(expireTime * 1000L);
        cacheWrapper.setRebuildCount(rebuildCount);
        cacheWrapper.setData(data);
        return cacheWrapper;
    }

    public boolean isValid() {
        return valid;
    }

    private void setValid(boolean valid) {
        this.valid = valid;
    }

    public D getData() {
        return data;
    }

    private void setData(D data) {
        this.data = data;
    }

    public long getCreateTime() {
        return createTime;
    }

    private void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    private void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getRebuildCount() {
        return rebuildCount;
    }

    private void setRebuildCount(int rebuildCount) {
        this.rebuildCount = rebuildCount;
    }
}
