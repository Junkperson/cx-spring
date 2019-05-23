package com.cxing.spring.formework.aop.aspect;

import com.cxing.spring.formework.aop.intercept.CXMethodInterceptor;
import com.cxing.spring.formework.aop.intercept.CXMethodInvocation;

import java.lang.reflect.Method;


public class CXAfterThrowingAdviceInterceptor extends CXAbstractAspectAdvice implements CXAdvice, CXMethodInterceptor {


    private String throwingName;

    public CXAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(CXMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
