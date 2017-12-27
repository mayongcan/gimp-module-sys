package com.gimplatform.module.sys.generator;

import java.net.URL;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.gimplatform.core.generator.GeneratorApi;

public class GeneratorMain {
	/**
	 * 请直接修改以下代码调用不同的方法以执行相关生成任务.
	 */
	public static void main(String[] args) throws Exception {
		//模板路径
		Resource templateResource = new ClassPathResource("GenCodeTemplate/");
		URL templateUrl = templateResource.getURL();
		
		GeneratorApi g = new GeneratorApi();
//		g.printAllTableNames();				//打印数据库中的表名称
//		g.deleteOutRootDir();				//删除生成器的输出目录
//		g.generateByAllTable(templateUrl.getPath());	
//		g.deleteByAllTable(templateUrl.getPath());
		//通过数据库表生成文件,template为模板的根目录
		g.generateByTable("oa_apply_leave", templateUrl.getPath());
//		g.deleteByTable("sys_user_info", templateUrl.getPath()); //删除生成的文件	
		//自动搜索数据库中的所有表并生成文件,template为模板的根目录
		
//		g.generateByClass(SmsInfo.class, templateUrl.getPath());
	}
}
