package com.chuan.spring.formework.context;

/**
 * 通过解耦的方式获得IOC容器的顶层设计
 * 后面通过一个监听器去扫描所有的类，只要实现了此接口
 * 将自动调用setApplicationContext()方法，从而将IOC容器注入到目标类中
 * author:曲终、人散
 * Date:2019/4/24 20:20
 */
public interface GPApplicationContextAware {

    void setApplicationContext(GPApplicationContext applicationContext);
}
