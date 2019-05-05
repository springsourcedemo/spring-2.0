package com.chuan.spring.demo.aspect;

import com.chuan.spring.formework.aop.aspect.GPJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * author:曲终、人散
 * Date:2019/5/5 23:21
 */
@Slf4j
public class LogAspect {
    //在调用一个方法之前，执行 before 方法
    public void before(GPJoinPoint joinPoint) {
    //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    //在调用一个方法之后，执行 after 方法
    public void after(GPJoinPoint joinPoint) {
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    public void afterThrowing(GPJoinPoint joinPoint, Throwable ex) {
        log.info("出现异常" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }

}
