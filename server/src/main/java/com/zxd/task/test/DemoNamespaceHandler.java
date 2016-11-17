package com.zxd.task.test;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author zxd
 * @since 16/8/16.
 */
public class DemoNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("demo", new DemoBeanDefinitionParser());
    }
}
