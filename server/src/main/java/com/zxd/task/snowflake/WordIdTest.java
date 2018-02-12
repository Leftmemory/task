package com.zxd.task.snowflake;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/17.
 */
public class WordIdTest {

    public static void main(String[] args) {
        WorkIdGenerator2 workIdGenerator = new WorkIdGenerator2();
        workIdGenerator.init();

        workIdGenerator.getWorkId();
    }
}
