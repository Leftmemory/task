package com.zxd.task.service.impl;

import com.zxd.task.cache.aop.RedisCacheable;
import com.zxd.task.cache.aop.RedisClearable;
import com.zxd.task.cache.rebuild.RebuildCacheProcessor;
import com.zxd.task.service.Person;
import com.zxd.tast.common.result.Result;
import org.springframework.stereotype.Component;

/**
 * @author zxd
 * @since 16/5/19.
 */
@Component("programDeveloper")
public class ProgramDeveloper implements Person{
    @Override
    public void play() {
        System.out.println("play computer game!");
    }

    @Override
    public void testInner() {
        play();
    }
}
