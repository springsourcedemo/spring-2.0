package com.chuan.spring.formework.aop.aspect;

import com.chuan.spring.formework.aop.intercept.GPMethodInterceptor;
import com.chuan.spring.formework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:34
 */
public class GPAfterReturningAdvice extends GPAbstractAspectAdvice implements GPAdvice, GPMethodInterceptor {

    private GPJoinPoint joinPoint;

    public GPAfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    public void afterReturning(Object returnValue,Method method,Object[] args,Object target) throws Throwable{
        invokeAdviceMethod(this.joinPoint,returnValue,null);
    }


}
