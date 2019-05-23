package com.cxing.spring.formework.aop.aspect;

import com.cxing.spring.formework.aop.intercept.CXMethodInterceptor;
import com.cxing.spring.formework.aop.intercept.CXMethodInvocation;

import java.lang.reflect.Method;


public class CXMethodAroundAdviceInterceptor extends CXAbstractAspectAdvice implements CXAdvice, CXMethodInterceptor {


    private CXJoinPoint joinPoint;

    public CXMethodAroundAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void around(Method method,Object[] args,Object target) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }
    @Override
    public Object invoke(CXMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        System.out.println("环绕之前");
        this.joinPoint = mi;
        around(mi.getMethod(), mi.getArguments(), mi.getThis());
        System.out.println("环绕之后");
        return mi.proceed();
    }
}
