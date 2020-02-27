package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 15579
 * 文件说明：创建服务消费者工程 （使用feign方式）
 * Feign 是一个声明式的伪 Http 客户端，它使得写 Http 客户端变得更简单。
 * 使用 Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，
 * 可使用 Feign 注解和 JAX-RS 注解。Feign 支持可插拔的编码器和解码器。
 * Feign 默认集成了 Ribbon，并和 Eureka 结合，默认实现了负载均衡的效果
 */
@SpringBootApplication
@EnableFeignClients // @EnableFeignClients注解开启 Feign 功能
@EnableDiscoveryClient //通过 @EnableDiscoveryClient 注解开启注册到服务中心
// EnableDiscoveryClient代表的就是服务消费者注册到服务注册中心
@EnableHystrixDashboard     //@EnableHystrixDashboard  开启熔断器仪表盘监控
public class WebAdminFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAdminFeignApplication.class,args);
    }
}
