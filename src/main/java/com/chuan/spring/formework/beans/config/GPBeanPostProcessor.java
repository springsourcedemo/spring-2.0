package com.chuan.spring.formework.beans.config;

/**
 * author:曲终、人散
 * Date:2019/5/1 16:03
 */
public class GPBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
