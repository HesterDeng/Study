package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * 文件说明：服务消费者工程（使用Ribbon方式） 服务消费者去调用服务提供者
 * Ribbon 是一个负载均衡客户端，可以很好的控制 http 和 tcp 的一些行为。
 */
@SpringBootApplication
//通过 @EnableDiscoveryClient 注解开启注册到服务中心
//@EnableDiscoveryClient代表的是服务消费者注册到服务注册中心
//此注解一般情况下是服务消费者使用
@EnableDiscoveryClient
@EnableHystrix      //@EnableHystrix  开启熔断器
public class WebAdminRibbonApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAdminRibbonApplication.class,args);
    }
}
