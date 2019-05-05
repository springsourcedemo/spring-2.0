package com.chuan.spring.formework.aop.intercept;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:38
 */
public interface GPMethodInterceptor {

    Object invoke(GPMethodInvocation invocation) throws Throwable;
}
