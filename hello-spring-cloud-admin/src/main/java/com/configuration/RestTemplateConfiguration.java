package com.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: 刘毅
 * @date: 2020/4/3 9:24
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory clientConfig = new SimpleClientHttpRequestFactory();
        clientConfig.setReadTimeout(20000);
        clientConfig.setConnectTimeout(10000);
        return new RestTemplate(clientConfig);
    }
}
