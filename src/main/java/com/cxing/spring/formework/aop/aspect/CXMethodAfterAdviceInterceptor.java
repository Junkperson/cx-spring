package com.cxing.spring.formework.aop.aspect;

import com.cxing.spring.formework.aop.intercept.CXMethodInterceptor;
import com.cxing.spring.formework.aop.intercept.CXMethodInvocation;

import java.lang.reflect.Method;


public class CXMethodAfterAdviceInterceptor extends CXAbstractAspectAdvice implements CXAdvice, CXMethodInterceptor {


    private CXJoinPoint joinPoint;

    public CXMethodAfterAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void after(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }
    @Override
    public Object invoke(CXMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        Object proceed = mi.proceed();
        after(mi.getMethod(), mi.getArguments(), mi.getThis());
        return proceed;
    }
}
