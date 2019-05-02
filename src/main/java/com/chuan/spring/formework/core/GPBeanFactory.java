package com.chuan.spring.formework.core;

/**
 * 工厂单例的顶层设计
 * author:曲终、人散
 * Date:2019/4/24 19:53
 */
public interface GPBeanFactory {
    /**
     * 根据neanName从ioc容器中容器中获取实例Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;

}

