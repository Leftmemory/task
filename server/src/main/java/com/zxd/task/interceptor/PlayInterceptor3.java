package com.zxd.task.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 * @author zxd
 * @since 17/3/25.
 */
public class PlayInterceptor3 implements MethodInterceptor{
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("------interceptor3");
        return invocation.proceed();
    }
}
