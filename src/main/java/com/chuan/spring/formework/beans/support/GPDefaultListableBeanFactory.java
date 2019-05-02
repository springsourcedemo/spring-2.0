package com.chuan.spring.formework.beans.support;


import com.chuan.spring.formework.beans.config.GPBeanDefinition;
import com.chuan.spring.formework.context.sppport.GPAbstractAppolicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:曲终、人散
 * Date:2019/4/24 20:04
 */
public class GPDefaultListableBeanFactory extends GPAbstractAppolicationContext {

    //存储注册信息的BeanDefinition ,伪ioc容器
    public final Map<String, GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, GPBeanDefinition>(256);
}
