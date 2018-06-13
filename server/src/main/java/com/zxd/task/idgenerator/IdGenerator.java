package com.zxd.task.idgenerator;

import com.zxd.task.snowflake.SnowFlakeIdGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/15.
 */
public class IdGenerator {

    private static LinkedBlockingQueue<Long> queue = new LinkedBlockingQueue<>(4096);

    private SnowFlakeIdGenerator snowFlakeIdGenerator = new SnowFlakeIdGenerator();

    public Long getId(){
        try {
            return queue.poll(10L, TimeUnit.MILLISECONDS);
//            return queue.poll();
        }catch (Exception e){

            System.out.println("generate id timeout");
            return null;
        }
    }

    public void start(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @SuppressWarnings("InfiniteLoopStatement")
            @Override
            public void run() {
                for(;;) {
                    long id = snowFlakeIdGenerator.getId();
                    for (long i = 0; i < 4095; i++) {
                        try {
                            queue.put(id + i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("put queue error");
                        }
                    }
                }
            }
        });
    }
}
