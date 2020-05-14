package file.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tunnelkey.tktim.model.base.file.FileInfoTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
* @Version: 0.0.1
* @Description: 初始化文件类型参数
* @Author: lxt
* @Date:  2019/1/18 13:26
*/
@Configuration
public class FileTypesMappingConfiguration {

    @Bean
    public FileInfoTypes getFileTypes(){
        FileInfoTypes fileInfoTypes = new FileInfoTypes();
        try {
            //读取classpath中json文件
            ClassPathResource resource = new ClassPathResource("config/FileTypeMapping.json");
            //UTF8格式读取字符
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            //读取每行数据进行拼接，不然会在之后的解析报错
            StringBuilder stringBuilder = new StringBuilder();
            String temp;
            while ( (temp = reader1.readLine()) != null){
                stringBuilder.append(temp);
            }
            fileInfoTypes.fileTypeMapping = new HashMap<>();
            //fastJSON解析为对象
            JSONObject jsonObject = JSON.parseObject(stringBuilder.toString());
            jsonObject.forEach((k,v) -> fileInfoTypes.fileTypeMapping.put(k,(List<String>) v));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInfoTypes;
    }
}
