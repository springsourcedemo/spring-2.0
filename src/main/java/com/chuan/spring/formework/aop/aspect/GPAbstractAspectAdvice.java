package com.chuan.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * author:曲终、人散
 * Date:2019/5/5 22:35
 */
public class GPAbstractAspectAdvice {

    private Method aspectMethod;

    private Object aspectTarget;

    public GPAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }
    public Object invokeAdviceMethod(GPJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{

        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if(null == parameterTypes || parameterTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if(parameterTypes[i] == GPJoinPoint.class){
                    args[i] = joinPoint;
                }
                if(parameterTypes[i] == Throwable.class){
                    args[i] = tx;
                }
                if(parameterTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }
    }
}
