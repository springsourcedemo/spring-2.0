package com.chuan.spring.formework.context.sppport;

/**
 * ioc容器实现的顶层设计
 * author:曲终、人散
 * Date:2019/4/24 19:58
 */
public abstract class GPAbstractAppolicationContext {

    //受保护的，提供给子类重写
    protected void refresh() throws Exception{

    }

}
