package com.configuration;

import com.tunnelkey.tktim.api.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
* @version:（1.0.0.0）
* @Description: （注入日志类，全包统一使用）
* @author: enoch
* @date: 2018年11月2日 下午3:25:35
 */
@Configuration
public class LogConfig {
	@Bean
	@Scope("singleton")
	public Logger getLogger(){
	   return  LoggerFactory.getLogger(Application.class);
	}
}
