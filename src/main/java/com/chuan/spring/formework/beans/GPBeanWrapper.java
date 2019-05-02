package com.chuan.spring.formework.beans;


import lombok.Data;

/**
 * author:曲终、人散
 * Date:2019/4/24 21:32
 */
@Data
public class GPBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public GPBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    //返回代理以后的class
    //可能会是这个$Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
