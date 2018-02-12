package com.zxd.task.test.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zxd
 * @since 17/2/26.
 */
public class JDKDynamicProxy implements InvocationHandler {

    private Object target;

    public JDKDynamicProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("test1")) {
            System.out.println("------ before -----");
            Object result = method.invoke(target,args);
            System.out.println("------ after -----");
            return result;

        }else {
            return method.invoke(target,args);
        }
    }

    public Object createProxy(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                target.getClass().getInterfaces(), this);
    }

    public static void main(String[] args) {
        //生成$Proxy0的class文件
//        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        JDKDynamicTest jdkDynamicTest = new JDKDynamicTestImpl();
        JDKDynamicProxy proxy = new JDKDynamicProxy(jdkDynamicTest);
        jdkDynamicTest = (JDKDynamicTest)proxy.createProxy();
//        jdkDynamicTest.test1();
        jdkDynamicTest.testInner();
    }
}
