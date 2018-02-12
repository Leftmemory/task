package com.zxd.task.cache.aop;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.zxd.task.cache.CacheWrapper;
import com.zxd.task.cache.JedisClient;
import com.zxd.task.cache.rebuild.RebuildCacheManager;
import com.zxd.tast.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * redis 缓存注解拦截，
 *
 * @author zxd
 * @since 16/12/1.
 */
//@Aspect
//@Component
@Slf4j
public class RedisCacheableAopSupport implements Ordered {

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private RebuildCacheManager rebuildCacheManager;

    private final static int defaultInvalidExpire = 10;//无效结果默认缓存时间

    private ExpressionParser parser = new SpelExpressionParser();

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * 查询接口缓存拦截，对结果做缓存
     *
     * @throws Throwable
     */
    @Around(value = "@annotation(com.zxd.task.cache.aop.RedisCacheable)" +
            "&&@annotation(redisCacheable)&&execution(com.zxd.tast.common.result.Result com.zxd.task..*.*(..))")
    public Result buildProcess(final ProceedingJoinPoint pjp, RedisCacheable redisCacheable) throws Throwable {
        String cacheKey = null;
        try {
            Method method = getMethod(pjp);
            final String keyExt = parseKey(redisCacheable.keyExt(), method, pjp.getArgs());
            cacheKey = redisCacheable.key() + keyExt;
            //key为空，不做缓存
            if (StringUtils.isBlank(cacheKey)) {
                return (Result) pjp.proceed();
            }

            CacheWrapper cacheWrapper = jedisClient.getHash(cacheKey, cacheKey, CacheWrapper.class);
            //缓存不为空
            if (cacheWrapper != null) {
                //检测缓存新鲜度
                checkAndRebuildCache(pjp, redisCacheable.expireTime(), cacheKey, cacheWrapper);
                return (Result) cacheWrapper.getData();
            }
        } catch (Exception e) {
            log.error("RedisCacheableAopSupport buildProcess error", e);
        }

        //执行具体方法
        Result result = (Result) pjp.proceed();
        if (result == null) {
            return null;
        }
        //构建缓存
        buildCache(cacheKey, result, redisCacheable.expireTime(), 1);

        return result;

    }

    /**
     * 检测缓存新鲜度，重建缓存
     */
    private void checkAndRebuildCache(final ProceedingJoinPoint pjp,
                                      final long expireTimeNew,
                                      final String cacheKey,
                                      CacheWrapper cacheWrapper) {
        long curTime = System.currentTimeMillis();
        long createTime = cacheWrapper.getCreateTime();
        long expireTime = cacheWrapper.getExpireTime();
        final int rebuildCount = cacheWrapper.getRebuildCount();
        //按次数，逐次增加需要重建时间
        long checkTime = (long) (expireTime * (1.0 - (1.0 / (1.0 + rebuildCount))));
        if ((curTime - createTime) > checkTime) {
            //异步重建缓存
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Result rebuildInfo = (Result) pjp.proceed();
                        if (rebuildInfo == null) {
                            return;
                        }
                        buildCache(cacheKey, rebuildInfo, expireTimeNew, rebuildCount + 1);
                    } catch (Throwable t) {
                        log.error("重建缓存异常", t);
                    }
                }
            });
        }
    }

    private void buildCache(String cacheKey, Result result, long expireTime, int rebuildCount) {
        //异常情况直接返回
        if (!result.isSuccess()) {
            CacheWrapper cacheWrapper = CacheWrapper.wrapInValid(result, System.currentTimeMillis(),
                    expireTime, rebuildCount);
            jedisClient.setHash(cacheKey, cacheKey, cacheWrapper, JedisClient.CACHE_EXP_MINUTES * 10);
            return;
        }
        Object obj = result.getData();
        boolean check = isValidResult(obj);
        //结果有效性校验,有效设置正常缓存时间，否则，设置短期缓存
        if (check) {
            CacheWrapper cacheWrapper = CacheWrapper.wrapValid(result, System.currentTimeMillis(),
                    expireTime, rebuildCount);
            jedisClient.setHash(cacheKey,cacheKey, cacheWrapper, Long.valueOf(expireTime).intValue());
        } else {
            CacheWrapper cacheWrapper = CacheWrapper.wrapValid(result, System.currentTimeMillis(),
                    defaultInvalidExpire, rebuildCount);
            jedisClient.setHash(cacheKey,cacheKey, cacheWrapper, defaultInvalidExpire);
        }
    }

    /**
     * 保存或发布接口拦截， 清理或重建缓存
     * 重建需要业务方实现重建接口
     *
     * @throws Throwable
     */
    @Around(value = "@annotation(com.zxd.task.cache.aop.RedisClearable)" +
            "&&@annotation(redisClearable)&&execution(com.zxd.tast.common.result.Result com.zxd.tast..*.*(..))")
    public Result delOrRebuildProcess(ProceedingJoinPoint pjp, final RedisClearable redisClearable) throws Throwable {

        //先执行处理逻辑
        final Result result = (Result) pjp.proceed();
        if (!result.isSuccess()) {
            return result;
        }
        try {
            Method method = getMethod(pjp);
            final String keyExt = parseKey(redisClearable.keyExt(), method, pjp.getArgs());
            final String cacheKey = redisClearable.key() + keyExt;
            //是否需要重建，不需要，删除缓存,结束
            if (!redisClearable.isNeedRebuild()) {
                jedisClient.delete(cacheKey);
                return result;
            }
            //异步重建缓存
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Result rebuildInfo = rebuildCacheManager.getRebuildInfo(redisClearable.key(), keyExt);
                    if (rebuildInfo == null) {
                        jedisClient.delete(cacheKey);
                        return;
                    }
                    buildCache(cacheKey, rebuildInfo, redisClearable.expireTime(), 1);
                }
            });
        } catch (Exception e) {
            log.error("RedisCacheableAopSupport delOrRebuildProcess error", e);
        }
        return result;
    }


    /**
     * 获取被拦截方法对象
     * <p/>
     * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     * 而缓存的注解在实现类的方法上
     * 所以应该使用反射获取当前对象的方法对象
     */
    public Method getMethod(ProceedingJoinPoint pjp) {
        //获取参数的类型
        Class[] argTypes = ((MethodSignature) pjp.getSignature()).getMethod().getParameterTypes();

        Method method = null;
        try {
            method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), argTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return method;

    }

    /**
     * 获取缓存的key
     * key 缓存key前缀
     * keyExt 具体参数，支持SPEL表达式
     *
     * @return
     */

    private String parseKey(String keyExt, Method method, Object[] args) {
        try {
            if (StringUtils.isBlank(keyExt)) {
                return "";
            }
            //获取被拦截方法参数名列表(使用Spring支持类库)
            LocalVariableTableParameterNameDiscoverer u =
                    new LocalVariableTableParameterNameDiscoverer();
            String[] paraNameArr = u.getParameterNames(method);


            //SPEL上下文
            StandardEvaluationContext context = new StandardEvaluationContext();            //把方法参数放入SPEL上下文中
            for (int i = 0; i < paraNameArr.length; i++) {
                context.setVariable(paraNameArr[i], args[i]);
            }
            //使用SPEL进行key的解析
            return parser.parseExpression(keyExt).getValue(context, String.class);
        } catch (Exception e) {
            log.info("[RedisCacheableAopSupport] parseKey error", e);
        }
        return null;
    }

    private boolean isValidResult(Object obj) {
        if (obj == null) {
            return false;
        }

        //map 集合处理
        if (obj instanceof Map && MapUtils.isEmpty((Map) obj)) {
            return false;
        }
        //set或list 接好
        if (obj instanceof Collection && CollectionUtils.isEmpty((Collection) obj)) {
            return false;
        }
        return true;
    }

    @Override
    public int getOrder() {
        //放dubbo服务拦截器后面执行
        return 31;
    }
}