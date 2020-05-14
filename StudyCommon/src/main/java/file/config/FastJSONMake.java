package file.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version:（1.0.0.0）
 * @Description: 配置fastjson取代springboot的默认fackjson
 * @author: enoch
 * @date: 2018年11月2日 下午1:12:03
 */
@Configuration
@Order(1)
public class FastJSONMake implements WebMvcConfigurer{
    static {
        TypeUtils.compatibleWithJavaBean = true;
        TypeUtils.compatibleWithFieldName=true;//大写就大写，小写就小写
    }
    public HttpMessageConverter<Object> fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,//关闭循环引用
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,//允许输入为空的字段
                SerializerFeature.WriteNullListAsEmpty
        );
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }
    /**
     * 配饰新的对象json转换取代springboot默认exchange to fastjson的jackson
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter());
        System.out.println("exchange to fastjson");
    }
}
