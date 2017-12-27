package com.gimplatform.module.sys.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 资源服务器配置
 * @author zzd
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
	protected final Logger logger =  LoggerFactory.getLogger(this.getClass());

	private static final String RESOURCE_ID = "GIMP_RES_SYS";

	@Autowired
	private RedisConnectionFactory redisConnection;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.tokenServices(tokenServices()).resourceId(RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		logger.info("Initializing Auth Resource Server Configuration ");
		http.anonymous().disable()
			.requestMatchers().antMatchers("/**")
			.and()
			.authorizeRequests().antMatchers("/api*/**").permitAll()
			.and()
			.exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		
//		http.anonymous().disable()
//			.requestMatchers().antMatchers("/api*/**")
//			.and().authorizeRequests()
//			.antMatchers("/api*/**").permitAll()
//			.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}
	
    /**
     * 将token写入redis
     * @return
     */
	@Bean
	public TokenStore tokenStore() {
		return new RedisTokenStore(redisConnection);
	}
    
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
	
//	/**
//	 * 转换Jwt格式token
//	 * @return
//	 */
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("gimplatform");
//        return converter;
//    }

//  /**
//   * 生成JWT格式token
//   * @return
//   */
//  @Bean
//  public TokenStore tokenStore() {
//      return new JwtTokenStore(accessTokenConverter());
//  }
}