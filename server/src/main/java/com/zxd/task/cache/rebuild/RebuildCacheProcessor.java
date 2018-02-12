package com.zxd.task.cache.rebuild;

import com.zxd.tast.common.result.Result;

/**
 * 重建缓存接口
 * @author zxd
 * @since 16/12/14.
 */
public interface RebuildCacheProcessor {

    /**
     * 重建缓存实现接口，查询重建后数据，返回结构应该和查询接口一致
     * @param keyParam 注解删除时 keyExt 解析后数据
     * @return
     */
    Result rebuild(String keyParam);
}
