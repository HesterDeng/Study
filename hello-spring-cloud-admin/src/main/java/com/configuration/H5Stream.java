package com.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: 刘毅
 * @date: 2020/4/3 9:30
 */
@Component
@ConfigurationProperties(prefix = "hfivestream")
@Data
public class H5Stream {
    private String api;
    private String password;
    private String username;
}
