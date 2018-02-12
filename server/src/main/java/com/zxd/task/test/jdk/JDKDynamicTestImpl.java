package com.zxd.task.test.jdk;

/**
 * @author zxd
 * @since 17/2/26.
 */
public class JDKDynamicTestImpl implements JDKDynamicTest{
    @Override
    public void test1() {
        System.out.println("JDK - test1");
    }

    @Override
    public void testInner() {
        System.out.println("testInner begin");
        test1();
        System.out.println("testInner end!");
    }
}
