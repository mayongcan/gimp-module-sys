package com.gimplatform.module.sys.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 配置验证过滤
 * @author zzd
 *
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity.anonymous().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
    	//配置不拦截的API
        web.ignoring().antMatchers("/api/system/druidMonitor/**", "/api/system/district/**", "/api/system/ignore/**");
    }
}
