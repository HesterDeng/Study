package com.RestTemplate;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 文件说明：配置注入 RestTemplate 的 Bean，并通过 @LoadBalanced 注解表明开启负载均衡功能
 */
@Configuration
public class RestTemplateConfiguration {
    @Bean
    @LoadBalanced //注解表明开启负载均衡功能
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
