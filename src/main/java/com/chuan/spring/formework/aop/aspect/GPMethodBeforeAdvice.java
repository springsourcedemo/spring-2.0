package com.chuan.spring.formework.aop.aspect;

import com.chuan.spring.formework.aop.intercept.GPMethodInterceptor;
import com.chuan.spring.formework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:32
 */
public class GPMethodBeforeAdvice extends GPAbstractAspectAdvice implements GPAdvice, GPMethodInterceptor {

    private GPJoinPoint joinPoint;

    public GPMethodBeforeAdvice(Method aspectMethod,Object target) {
        super(aspectMethod,target);
    }

    public void before(Method method,Object[] args,Object target) throws Throwable{
        invokeAdviceMethod(this.joinPoint,null,null);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.before(mi.getMethod(),mi.getArguments(),mi.getThis());
        return mi.proceed();
    }
}
