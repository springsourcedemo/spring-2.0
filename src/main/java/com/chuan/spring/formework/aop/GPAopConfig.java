package com.chuan.spring.formework.aop;

import lombok.Data;

/**
 * author:曲终、人散
 * Date:2019/5/5 21:49
 */
@Data
public class GPAopConfig {

    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
