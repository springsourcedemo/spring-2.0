package com.chuan.spring.formework.aop;

import com.chuan.spring.formework.aop.intercept.GPMethodInvocation;
import com.chuan.spring.formework.aop.support.GPAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * author:曲终、人散
 * Date:2019/5/5 21:44
 */
public class GPJdkDynamicAopProxy implements GPAopProxy, InvocationHandler {

    private GPAdvisedSupport config;

    public GPJdkDynamicAopProxy(GPAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    //把原生对象传递进来
    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.config.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorAndDynamicMethodMatchers = config.getInterceptorAndDynamicInterceptionAdvice(method, this.config.getTargetClass());

        GPMethodInvocation invocation = new GPMethodInvocation(proxy,this.config.getTarget(),method,args,
                this.config.getTargetClass(),interceptorAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
