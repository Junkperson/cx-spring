package com.cxing.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
*
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CXController {
	String value() default "";
}
