package com.zxd.task.test.cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zxd
 * @since 16/12/22.
 */
public class CglibTestProxy implements MethodInterceptor {


    public Object createProxy(Class cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(method.getName().equals("test1")) {
            System.out.println("------ before -----");
            CglibTest cglibTest = new CglibTest();
            Object result = methodProxy.invokeSuper(cglibTest, objects);
//            Object result = methodProxy.invoke(cglibTest, objects);
            System.out.println("------ after -----");
            return result;

        }else {
            return methodProxy.invokeSuper(o, objects);
//            return methodProxy.invoke(o, objects);
        }
    }


    public static void main(String[] args) throws Exception{
        CglibTestProxy cglibTestInterceptor = new CglibTestProxy();
        CglibTest cglibTest =  (CglibTest)cglibTestInterceptor.createProxy(CglibTest.class);
        cglibTest.test1();
//        cglibTest.testInner();
//        System.in.read();
    }
}
