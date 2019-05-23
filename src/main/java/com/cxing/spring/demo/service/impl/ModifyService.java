package com.cxing.spring.demo.service.impl;

import com.cxing.spring.formework.annotation.CXService;
import com.cxing.spring.demo.service.IModifyService;

/**
 * 增删改业务
*
 */
@CXService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) {
		return "modifyService add,name=" + name + ",addr=" + addr;
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
