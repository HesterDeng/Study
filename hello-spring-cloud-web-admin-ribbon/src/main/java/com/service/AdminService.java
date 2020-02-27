package com.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AdminService {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "sayHiError")
    public String sayHi(String message) {
        //或者使用yml里面的别名
        return restTemplate.getForObject("http://hello-spring-cloud-service-admin/hi?message="+message,String.class);
    }
    /**
     * sayHi方法的熔断方法
     * @param message
     * @return
     */
    public String sayHiError(String message){
        return String.format("Hi SpringCloud Message:%s but request bad",message);
    }
}
