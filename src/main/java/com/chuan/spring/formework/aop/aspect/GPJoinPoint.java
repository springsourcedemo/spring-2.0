package com.chuan.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:09
 */
public interface GPJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
