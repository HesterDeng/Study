package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 2019/6/17 10:12
 * 文件说明：服务链路追踪工程
 * ZipKin 是一个开放源代码的分布式跟踪系统，由 Twitter 公司开源
 * ，它致力于收集服务的定时数据，以解决微服务架构中的延迟问题，
 * 包括数据的收集、存储、查找和展现。它的理论模型来自于 Google Dapper 论文。
 *
 * 每个服务向 ZipKin 报告计时数据，ZipKin 会根据调用关系通过 ZipKin UI 生成依赖关系图，
 * 显示了多少跟踪请求通过每个服务，该系统让开发者可通过一个 Web 前端轻松的收集和分析数据，
 * 例如用户每次请求服务的处理时间等，可方便的监测系统中存在的瓶颈。
 */
@SpringBootApplication
@EnableEurekaClient         //@EnableEurekaClient 开启注册到服务注册与发现 @EnableEurekaClient代表这是个服务提供者
public class ZipKinApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZipKinApplication.class,args);
    }
}
