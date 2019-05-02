package com.chuan.spring.formework.annotation;

import java.lang.annotation.*;


/**
 * author:曲终、人散
 * Date:2019/4/24 19:45
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPService {
	String value() default "";
}
