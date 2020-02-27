package com.hystrix;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HystrixDashboardConfiguration {
    @Bean   //注入bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();  //创建自己的servlet
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);  //注册servlet
        registrationBean.setLoadOnStartup(1);               //启动顺序
        registrationBean.addUrlMappings("/hystrix.stream");     //Servlet的访问路径
        registrationBean.setName("HystrixMetricsStreamServlet");//Servlet的名字
        return registrationBean;
    }
}
