package com.gimplatform.module.sys.conf;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * 配置Druid的监控统计功能
 * @author zzd
 *
 */
@Configuration
public class DruidConfiguration {

    private static final Logger logger = LogManager.getLogger(DruidConfiguration.class);

	@Bean
	public ServletRegistrationBean druidServlet() {
		logger.info("初始化数据源监控后台...");
	    ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
	    servletRegistrationBean.setServlet(new StatViewServlet());
	    //访问路径：apiGateway + /api/system/druidMonitor/login.html
	    servletRegistrationBean.addUrlMappings("/api/system/druidMonitor/*");
	    Map<String, String> initParameters = new HashMap<String, String>();
	    initParameters.put("loginUsername", "admin");		// 用户名
	    initParameters.put("loginPassword", "admin");		// 密码
	    initParameters.put("resetEnable", "false");			// 禁用HTML页面上的“Reset All”功能
	    initParameters.put("allow", ""); 					// IP白名单 (没有配置或者为空，则允许所有访问)
	    //initParameters.put("deny", "192.168.20.38");// IP黑名单 (存在共同时，deny优先于allow)
	    servletRegistrationBean.setInitParameters(initParameters);
	    return servletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		logger.info("初始化数据源监控后台过滤器...");
	    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
	    filterRegistrationBean.setFilter(new WebStatFilter());
	    filterRegistrationBean.addUrlPatterns("/*");
	    filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/api/system/druidMonitor/*");
	    return filterRegistrationBean;
	}

}
