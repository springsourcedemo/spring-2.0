package com.chuan.spring.demo.service;


/**
 * author:曲终、人散
 * Date:2019/4/24 19:45
 */
public interface IModifyService {

	/**
	 * 增加
	 */
	public String add(String name, String addr);
	
	/**
	 * 修改
	 */
	public String edit(Integer id, String name);
	
	/**
	 * 删除
	 */
	public String remove(Integer id);
	
}
