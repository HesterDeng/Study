package com.hester.hellospringcloudserviceadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient  //开启服务客户端向服务端注册功能
public class HelloSpringCloudServiceAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSpringCloudServiceAdminApplication.class, args);
    }

}
