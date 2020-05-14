package file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * @Version 0.0.1
 * @Description 文件上传临时文件路径配置
 * @Author lxt
 * @Date 2019/4/11 9:26
 */
@Configuration
public class MultipartConfig {
    @Value("${multipartConfig.temp.filePath}")
    private String filePath;

    @Bean
    MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        File file = new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
        factory.setLocation(filePath);
        return factory.createMultipartConfig();
    }
}
