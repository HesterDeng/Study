package com.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @version:（1.0.0.0）
 * @Description: websocket配置类
 * @author: enoch
 * @date: 2019/1/4 9:39
 */
@Configuration
public class websocketconfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
