package com.zxd.task;

import com.google.common.collect.Sets;
import com.zxd.task.snowflake.SnowFlakeIdGenerator;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/11.
 */
public class SnowFlakeIdGeneratorTest {

    @Test
    public void test() {
        SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator();
        Set<Long> set = new HashSet<>(Integer.MAX_VALUE);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        long id = idGenerator.getId();
                        boolean flag = set.add(id);
                        if(!flag) {
                            System.out.println(id);
                        }
                    }
                }
            }).start();
        }

        while (true){

        }
    }
}
