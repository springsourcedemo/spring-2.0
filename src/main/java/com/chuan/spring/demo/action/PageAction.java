package com.chuan.spring.demo.action;

import com.chuan.spring.demo.service.IQueryService;
import com.chuan.spring.formework.annotation.GPAutowired;
import com.chuan.spring.formework.annotation.GPController;
import com.chuan.spring.formework.annotation.GPRequestMapping;
import com.chuan.spring.formework.annotation.GPRequestParam;
import com.chuan.spring.formework.webmvc.servlet.GPModelAndView;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * author:曲终、人散
 * Date:2019/5/3 21:59
 */
@GPController
@GPRequestMapping("/")
public class PageAction {

    @GPAutowired
    IQueryService queryService;

    @GPRequestMapping("first.html")
    public GPModelAndView query(@GPRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("tescher",teacher);
        model.put("data",result);
        model.put("tpken","123456");
        return new GPModelAndView("first.html",model);

    }

}
