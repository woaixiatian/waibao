package com.bilibili.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Configuration
@EnableRedisHttpSession
public class SpringConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(getAuthInterceptor()).addPathPatterns("/**").excludePathPatterns("/error/**")
//				.excludePathPatterns("/gm/**");
//		registry.addInterceptor(getURLInterceptor()).addPathPatterns("/**").excludePathPatterns("/error/**")
//				.excludePathPatterns("/gm/**");
	}

	/*@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new StringHttpMessageConverter());
		converters.add(new ResultConverter());
		super.configureMessageConverters(converters);
	}*/

//	@Override
//	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//		converters.add(new ProtobufHttpMessageConverter());
//		super.configureMessageConverters(converters);
//	}

	/*@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.extendMessageConverters(converters);
	}*/

	/*@Bean
	public static ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}*/

	@Bean
	public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}
}