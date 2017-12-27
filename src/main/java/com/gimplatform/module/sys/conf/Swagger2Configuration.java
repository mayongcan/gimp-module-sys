package com.gimplatform.module.sys.conf;

import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Predicate;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 整合Swagger2
 * @author zzd
 *
 */
//@Configuration
//@EnableSwagger2
public class Swagger2Configuration {
    
    /**
     * 创建api文档工具
     * @return
     */
	@Bean
    public Docket createRestApi() { 
		Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
                Class<?> declaringClass = input.declaringClass();
                if (declaringClass == BasicErrorController.class)// 排除
                    return false;
                if(declaringClass.isAnnotationPresent(RestController.class)) // 被注解的类
                    return true;
                if(input.isAnnotatedWith(ResponseBody.class)) // 被注解的方法
                    return true;
                return false;
            }
        };
		return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(predicate)
            .apis(RequestHandlerSelectors.basePackage("com.gimplatform.module.sys.restful"))
            .paths(PathSelectors.any())
            .build();                                   
    }
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
	        .title("通用信息管理平台--RESTful APIs")
	        .description("通用信息管理平台(系统功能接口)")
	        //.termsOfServiceUrl("")
	        .contact(new Contact("zzd", "", "zzhdong@gmail.com"))
	        .version("1.0.2")
	        .build();
    }
}
