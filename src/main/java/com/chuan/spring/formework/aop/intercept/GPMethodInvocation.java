package com.chuan.spring.formework.aop.intercept;

import com.chuan.spring.formework.aop.aspect.GPJoinPoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:03
 */
public class GPMethodInvocation implements GPJoinPoint {
    protected  Object proxy;
    protected  Object target;
    protected  Method method;
    protected Object[] arguments ;
    private  Class<?> targetClass;
    private Map<String, Object> userAttributes;
    protected  List<?> interceptorsAndDynamicMethodMatchers;

    //定义一个索引，从-1开始，记录当前拦截器的位置
    private int currentInterceptorIndex = -1;

    public GPMethodInvocation(
            Object proxy,  Object target, Method method,  Object[] arguments,
             Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable{
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1){
            //自己本身的方法
            return this.method.invoke(this.target,this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if(interceptorOrInterceptionAdvice instanceof GPMethodInterceptor){
            GPMethodInterceptor mi =  (GPMethodInterceptor)interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else {
            //调用下一个Interceptor
           return proceed();
        }
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {

    }

    @Override
    public Object getUserAttribute(String key) {
        return null;
    }
}
