package com.cxing.spring.formework.aop.intercept;


public interface CXMethodInterceptor {
    Object invoke(CXMethodInvocation invocation) throws Throwable;
}
