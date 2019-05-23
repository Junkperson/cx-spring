package com.cxing.spring.demo.action;

import com.cxing.spring.demo.service.IQueryService;
import com.cxing.spring.demo.service.IModifyService;
import com.cxing.spring.formework.annotation.CXAutowired;
import com.cxing.spring.formework.annotation.CXController;
import com.cxing.spring.formework.annotation.CXRequestMapping;
import com.cxing.spring.formework.annotation.CXRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
*
 */
@CXController
@CXRequestMapping("/web")
public class MyAction {

	@CXAutowired
    IQueryService queryService;
	@CXAutowired
    IModifyService modifyService;

	@CXRequestMapping("/query.json")
	public String query(HttpServletRequest request, HttpServletResponse response,
					  @CXRequestParam("name") String name){
		String result = queryService.query(name);
//		out(response,result);
        return "404";
	}
	
	@CXRequestMapping("/add*.json")
	public void add(HttpServletRequest request, HttpServletResponse response,
					@CXRequestParam("name") String name, @CXRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@CXRequestMapping("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @CXRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@CXRequestMapping("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@CXRequestParam("id") Integer id,
			@CXRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
