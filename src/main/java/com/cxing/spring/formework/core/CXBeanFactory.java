package com.cxing.spring.formework.core;

public interface CXBeanFactory {

    /**
     * 根据beanName从IOC容器中获取一个实例Bean
     * @param beanMame
     * @return
     */
    Object getBean(String beanMame) throws Exception ;


    Object getBean(Class<?> beanClass) throws Exception;

}
