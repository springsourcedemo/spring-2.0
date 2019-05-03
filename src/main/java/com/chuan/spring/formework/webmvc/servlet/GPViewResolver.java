package com.chuan.spring.formework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * 设计这个类的目的是：
 * 1、将一个静态文件变为一个动态文件
 * 2、根据用户传送的参数不同，产生不同的结果
 * 最终输出字符串，交给Response输出
 * author:曲终、人散
 * Date:2019/5/3 20:04
 */
public class GPViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;

    public GPViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public GPView resolveViewName(String viewName, Locale locale){
        if(null == viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new GPView(templateFile);
    }
}
