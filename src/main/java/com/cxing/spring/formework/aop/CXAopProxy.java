package com.cxing.spring.formework.aop;


public interface CXAopProxy {


    Object getProxy();


    Object getProxy(ClassLoader classLoader);
}
