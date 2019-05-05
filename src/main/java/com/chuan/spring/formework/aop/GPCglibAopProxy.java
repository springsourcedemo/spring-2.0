package com.chuan.spring.formework.aop;

import com.chuan.spring.formework.aop.support.GPAdvisedSupport;

/**
 * author:曲终、人散
 * Date:2019/5/5 21:41
 */
public class GPCglibAopProxy implements GPAopProxy {

    private GPAdvisedSupport config;

    public GPCglibAopProxy(GPAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
