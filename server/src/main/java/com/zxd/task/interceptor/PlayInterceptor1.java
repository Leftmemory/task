package com.zxd.task.interceptor;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author zxd
 * @since 16/5/19.
 */
public class PlayInterceptor1 implements MethodBeforeAdvice, AfterReturningAdvice {


    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("open computer 1!");
    }

    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {
        System.out.println("close computer 1!");
    }
}
