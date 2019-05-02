package com.chuan.spring.demo.action;


import com.chuan.spring.demo.service.IModifyService;
import com.chuan.spring.demo.service.IQueryService;
import com.chuan.spring.formework.annotation.GPAutowired;
import com.chuan.spring.formework.annotation.GPController;
import com.chuan.spring.formework.annotation.GPRequestMapping;
import com.chuan.spring.formework.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * author:曲终、人散
 * Date:2019/4/24 19:45
 */
@GPController
@GPRequestMapping("/web")
public class MyAction {

	@GPAutowired
	IQueryService queryService;
	@GPAutowired
	IModifyService modifyService;

	@GPRequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
                      @GPRequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@GPRequestMapping("/add*.json")
	public void add(HttpServletRequest request, HttpServletResponse response,
                    @GPRequestParam("name") String name, @GPRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@GPRequestMapping("/remove.json")
	public void remove(HttpServletRequest request, HttpServletResponse response,
                       @GPRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@GPRequestMapping("/edit.json")
	public void edit(HttpServletRequest request, HttpServletResponse response,
                     @GPRequestParam("id") Integer id,
                     @GPRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}

	private void out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
