package com.zxd.task.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zxd
 * @since 16/12/17.
 */
@Component
@Slf4j
public class SpringIocUtil implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object getBean(Class<?> cls) throws BeansException {
        try {
            return applicationContext.getBean(cls);
        }catch (Exception e){
            log.info("SpringIocUtil-getBean error,", e);
            return null;
        }
    }

    public Object getBean(String name) throws BeansException {
        try {
            return applicationContext.getBean(name);
        }catch (Exception e){
            log.info("SpringIocUtil-getBean error,", e);
            return null;
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
