package com.cxing.spring.formework.aop;

import com.cxing.spring.formework.aop.support.CXAdvisedSupport;


public class CXCglibAopProxy implements  CXAopProxy {
    public CXCglibAopProxy(CXAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
