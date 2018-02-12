package com.zxd.task.cache.rebuild;

import com.google.common.collect.ImmutableMap;
import com.zxd.task.util.SpringIocUtil;
import com.zxd.tast.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author zxd
 * @since 16/12/14.
 */
@Component
@Slf4j
public class RebuildCacheManager {

    private ImmutableMap<String, RebuildCacheProcessor> immutableMap = null;

//    @Resource(name = "programDeveloper")
//    private RebuildCacheProcessor programDeveloper;

    @Autowired
    private SpringIocUtil springIocUtil;

    /**
     * 重建缓存封装接口
     *
     * @param key    缓存key前缀
     * @param keyExt 缓存key参数
     * @return 返回重建结果
     */
    public Result getRebuildInfo(String key, String keyExt) {
        try {
            RebuildCacheProcessor rebuildCacheProcessor = immutableMap.get(key);
            if (rebuildCacheProcessor == null) {
                return null;
            }
            return rebuildCacheProcessor.rebuild(keyExt);
        } catch (Exception e) {
            log.info("RebuildCacheManager - getRebuildInfo error,  key:" + key + " param:" + keyExt, e);
            return null;
        }
    }


    /**
     * bean 构建后调用
     */
    @PostConstruct
    private void postProcessor() {
        ImmutableMap.Builder<String, RebuildCacheProcessor> builder = ImmutableMap.builder();
        //增加实现类
        //存在循环引用，需要单独获取bean
//        programDeveloper = (RebuildCacheProcessor)springIocUtil.getBean("programDeveloper");
//        builder.put("rePlay", programDeveloper);
        immutableMap = builder.build();
    }
}
