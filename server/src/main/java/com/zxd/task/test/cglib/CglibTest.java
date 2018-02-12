package com.zxd.task.test.cglib;

/**
 * @author zxd
 * @since 16/12/22.
 */
public class CglibTest {

    public void test1(){
        System.out.println("Cglib - test1");
    }


    public void testInner(){
        System.out.println("testInner begin");
        test1();
        System.out.println("testInner end!");
    }
}
