package com.chuan.spring.formework.aop.aspect;

import com.chuan.spring.formework.aop.intercept.GPMethodInterceptor;
import com.chuan.spring.formework.aop.intercept.GPMethodInvocation;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:35
 */
public class GPAfterThrowingAdvice extends GPAbstractAspectAdvice implements GPAdvice, GPMethodInterceptor {

    @Setter
    private String throwingName;

    private GPMethodInvocation mi;

    public GPAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }
}
