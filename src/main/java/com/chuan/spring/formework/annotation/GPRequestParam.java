package com.chuan.spring.formework.annotation;

import java.lang.annotation.*;


/**
 * author:曲终、人散
 * Date:2019/4/24 19:45
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPRequestParam {
	
	String value() default "";
	
	boolean required() default true;

}
