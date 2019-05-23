package com.cxing.spring.formework.aop.aspect;

import java.lang.reflect.Method;

public interface CXJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);

}
