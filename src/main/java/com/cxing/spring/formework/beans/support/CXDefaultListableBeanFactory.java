package com.cxing.spring.formework.beans.support;

import com.cxing.spring.formework.beans.config.CXBeanDefinition;
import com.cxing.spring.formework.context.support.CXAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CXDefaultListableBeanFactory extends CXAbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected Map<String, CXBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, CXBeanDefinition>();

}
