package com.chuan.spring.formework.webmvc.servlet;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

/**
 * author:曲终、人散
 * Date:2019/5/3 21:24
 */
public class GPModelAndView {

    @Getter
    private String viewName;

    @Getter
    private Map<String,?> model;

    public GPModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public GPModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
