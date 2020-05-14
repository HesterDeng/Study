package utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: dengguiping
 * @Date: 2019/1/15 19:50
 * @Description:
 */
public class JSONHelper {

    public static Map<String, Map<String, String>> EMCErrorMap = new HashMap<>();

    public static Map<String, Map<String, String>> getErrorCode() {
        if (ObjectUtils.isEmpty(EMCErrorMap)) {
            JSONHelper jsonHelper = new JSONHelper();
            EMCErrorMap = jsonHelper.getJsonData("config/EMCCodeError.json");
        }
        return EMCErrorMap;
    }

    public Map<String, Map<String, String>> getJsonData(String fileName) {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            String json = FileUtils.readFileToString(resource.getFile(), "UTF-8");
            if (json != null) {
                return (Map<String, Map<String, String>>) JSON.parse(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
