package com.cxing.spring.formework.aop.config;

import lombok.Data;

@Data
public class CXAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAround;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
