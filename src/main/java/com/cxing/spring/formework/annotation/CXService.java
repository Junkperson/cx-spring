package com.cxing.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑,注入接口
*
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CXService {
	String value() default "";
}
