package com.gimplatform.module.sys.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.gimplatform.core.interceptor.LogInfoInterceptor;

/**
 * 配置站点相关设置
 * @author zzd
 *
 */
@Configuration
public class WebAppConfiguration {

    private static final Logger logger = LogManager.getLogger(WebAppConfiguration.class);
	
	/**
	 * 配置跨域访问
	 * @return
	 */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
    	logger.info("Initializing CORS Configuration ");
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                		.allowCredentials(true)
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .allowedOrigins("*");
            }
        };
    }
    
    /**
     * 添加相关拦截器
     * @return
     */
    @Bean
    public WebMvcConfigurer addWebInterceptors() {
    	logger.info("Initializing WebInterceptors Configuration ");
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) { 
            	logger.info("Initializing LogInterceptor "); 
            	//添加日志拦截器
                registry.addInterceptor(new LogInfoInterceptor()).addPathPatterns("/api/**");  
            }  
        };
    }
}