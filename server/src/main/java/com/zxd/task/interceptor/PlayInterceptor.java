package com.zxd.task.interceptor;

import com.zxd.task.service.impl.ProgramDeveloper;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author zxd
 * @since 16/5/19.
 */
@Aspect
@Component
public class PlayInterceptor {

    @Pointcut("execution(void *.play111())")
    public void playPoint(){}

    @Before("playPoint()")
    public void beforePlay(){
        System.out.println("open computer!");
    }

    @AfterReturning("playPoint()")
    public void afterPlay(){
        System.out.println("close computer!");

    }
}
