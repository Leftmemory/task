package com.zxd.task.main;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/3/1.
 */
public class ConstructTest {

    public ConstructTest(int i){
        System.out.println("---" + i);
    }

    public ConstructTest(String s) {
        System.out.println("----" + s);
    }

    public ConstructTest(String s, int i) {
        this(i);
//        this(s);
        System.out.println("---" + s + "---" + i);
    }


}
