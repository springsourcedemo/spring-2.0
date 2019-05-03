package com.chuan.spring.formework.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * author:曲终、人散
 * Date:2019/5/3 19:59
 */
@Data
public class GPHandlerMapping {

    private Object controller;	//保存方法对应的实例
    private Method method;		//保存映射的方法
    private Pattern pattern;    //URL的正则匹配

    public GPHandlerMapping(Pattern pattern , Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
