package com.chuan.spring.demo.service.impl;


import com.chuan.spring.demo.service.IModifyService;
import com.chuan.spring.formework.annotation.GPService;


/**
 * author:曲终、人散
 * Date:2019/4/24 19:45
 */
@GPService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception{

		throw new Exception("这是我自己创建的一个异常！！！");
//		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
