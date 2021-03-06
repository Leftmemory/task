package com.zxd.task.cache.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zxd
 * @since 16/12/1.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCacheable {

    /**
     * 缓存key前缀
     * @return
     */
    String key();

    /**
     * 缓存key具体参数，支持SPEL表达式； 完整缓存key为  key +  keyExt
     * @return
     */
    String keyExt() default "";

    /**
     * 缓存过期时间， 单位：秒， 允许为空; 使用默认有效期
     * @return
     */
    long expireTime() default -1L;
}
