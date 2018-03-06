package com.gimplatform.module.sys;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.gimplatform.module.sys.listen.StartupListener;

/**
 * 系统服务启动主函数 注：@SpringBootApplication注解等价于以默认属性使用@Configuration，@EnableAutoConfiguration和@ComponentScan：
 * @author zzd 
 */
@SpringBootApplication
@EnableEurekaClient
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SystemApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.addListeners(new StartupListener());
        app.run(args);
    }
}
