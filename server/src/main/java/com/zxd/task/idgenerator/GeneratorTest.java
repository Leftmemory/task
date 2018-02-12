package com.zxd.task.idgenerator;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/15.
 */
public class GeneratorTest {

    public static void main(String[] args) {
        IdGenerator idGenerator = new IdGenerator();
        idGenerator.start();

        Executor executor = Executors.newFixedThreadPool(4);
        for(int i=0; i< 100; i ++) {
            executor.execute(new Runnable() {
                @SuppressWarnings("InfiniteLoopStatement")
                @Override
                public void run() {
                    Long id = null;
                    for (; ; ) {
                        id = idGenerator.getId();
                        System.out.println("------  " + id + " thread " + Thread.currentThread());
                    }
                }
            });

        }
    }
}
