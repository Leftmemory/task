package com.zxd.task.test;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author zxd
 * @since 16/8/16.
 */
public class DemoBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return XsdDemo.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String name = element.getAttribute("name");
        String value = element.getAttribute("value");

        String isSelf = element.getAttribute("isSelf");


        if (StringUtils.hasText(name)) {
            builder.addPropertyValue("name", name);
        }
        if (StringUtils.hasText(value)) {
            builder.addPropertyValue("value", value);
        }

        if(StringUtils.hasText(isSelf)){
            builder.addPropertyValue("isSelf", isSelf);
        }
    }
}
