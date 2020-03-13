package com;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author 15579
 * 文件说明：SpringCloud微服务架构的监控管理工程（服务端）
 * 随着开发周期的推移，项目会不断变大，切分出的服务也会越来越多，
 * 这时一个个的微服务构成了错综复杂的系统。对于各个微服务系统的
 * 健康状态、会话数量、并发数、服务资源、延迟等度量信息的收集就成为了一个挑战。
 * Spring Boot Admin 应运而生，它正式基于这些需求开发出的一套功能强大的监控管理系统
 */
@SpringBootApplication
@EnableEurekaClient    //开启注册到Eureka服务注册与发现 EnableEurekaClient表示这是一个服务提供者
@EnableAdminServer      //开启SpringBootAdmin监控管理系统  服务端
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
    }
}
