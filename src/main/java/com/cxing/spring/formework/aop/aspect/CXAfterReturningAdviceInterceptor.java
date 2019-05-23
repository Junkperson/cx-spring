package com.cxing.spring.formework.aop.aspect;

import com.cxing.spring.formework.aop.intercept.CXMethodInterceptor;
import com.cxing.spring.formework.aop.intercept.CXMethodInvocation;

import java.lang.reflect.Method;


public class CXAfterReturningAdviceInterceptor extends CXAbstractAspectAdvice implements CXAdvice, CXMethodInterceptor {

    private CXJoinPoint joinPoint;

    public CXAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(CXMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
