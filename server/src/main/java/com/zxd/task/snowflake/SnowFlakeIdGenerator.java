package com.zxd.task.snowflake;

/**
 * 序列生成器
 *
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/11.
 * <p>
 * <p>
 * 基于twitter SnowFlake 算法， 生成 64位 Long型 id 支持单机每毫秒 2^12 共4096 个序列
 * <p>
 * <p>
 * 第1位为0，符号位。第2-42位表示毫秒数，共41位，当前时间200毫秒-2017年04月01日的毫秒数。
 * 第43-52位表示workId，即机器id，共10位，能支持1024台机器。第53-64位表示序列号，共12位
 * </p>
 */
public class SnowFlakeIdGenerator {

    private final static long SEQUENCE_BITS = 12;//序列12位

    private final static long WOKE_ID_BITS = 10;// 机器10位

    private final static long WOKE_ID_MAX = 1 << 10; //workId最大值

    private final static long SEQUENCE_ID_MAX = (1 << 12) - 1;// 序列最大值

    private final static long WOKE_ID_LEFT_SHIFT = SEQUENCE_BITS; //WORK_ID 左移位数

    private final static long TIMESTAMP_LEFT_SHIT = SEQUENCE_BITS + WOKE_ID_BITS;// 时间戳左移位数

    private final static long START_TIMESTAMP = 1501516800000L;// 初始时间戳 2017/8/1 0:0:0

    private long lastTime;

    private long sequence;

    public synchronized long getId() {

        long curTime = System.currentTimeMillis();
        //当前时间戳比上次还小，可能是时钟调整
        if (curTime < lastTime) {
            curTime = lastTime;
        }

        if (curTime == lastTime) {
            //自增1，超过最大值，取模，位运算比取模性能更好
            sequence = (sequence + 1) & SEQUENCE_ID_MAX;
            //超过该毫秒序列最大值，需等待下一毫秒
            if (sequence == 0) {
                curTime = nextTimeStamp(curTime);
            }
        } else {
            sequence = 0;
        }

        //重置上次时间
        lastTime = curTime;

        return (curTime - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIT
                | getWorkId() << WOKE_ID_LEFT_SHIFT
                | sequence;
    }


    private long nextTimeStamp(long curTime) {
        long next = System.currentTimeMillis();
        while (next <= curTime) {
            next = System.currentTimeMillis();
        }
        return next;
    }

    private long getWorkId() {
        long workId = 1L;
        if(workId < 0 || workId > WOKE_ID_MAX){
            throw new RuntimeException("机器名id生成错误");
        }
        return workId;
    }
}
