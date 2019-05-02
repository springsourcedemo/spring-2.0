package com.chuan.spring.demo.service.impl;

import com.chuan.spring.demo.service.IQueryService;
import com.chuan.spring.formework.annotation.GPService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * author:曲终、人散
 * Date:2019/4/24 19:45
 */
@GPService
@Slf4j
public class QueryService implements IQueryService {

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		log.info("这是在业务方法中打印的：" + json);
		return json;
	}

}
