package com.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.util.TypeUtils;
import com.tunnelkey.tktim.api.interceptor.AuthorizeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Order(value = 2)
public class InterceptorConfiguration extends WebMvcConfigurationSupport {
    static {
        TypeUtils.compatibleWithJavaBean = true;
        TypeUtils.compatibleWithFieldName = true;//大写就大写，小写就小写
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excudeList = new ArrayList<>();
        excudeList.add("/get");
        excudeList.add("/init");//初始化保留
        excudeList.add("/test");//初始化保留
        excudeList.add("/api/login");
        excudeList.add("/api/login/remplogin");
        excudeList.add("/api/OauthLogin");
        excudeList.add("/api/DNLogin");
        excudeList.add("/api/QrLoginCode");
        excudeList.add("/api/file/get");
        excudeList.add("/api/file/ExportDBXlExcel");
        excudeList.add("/api/file/ExportBdData");
        excudeList.add("/api/file/ExportXlData");
        excudeList.add("/api/file/ExportXlLegendState");
        excudeList.add("/api/file/show/*");
        excudeList.add("/api/file/UserMenu");
        excudeList.add("/api/file/codelist");
        excudeList.add("/api/file/GetCodeList");
        excudeList.add("/api/file/downloadZip");
        excudeList.add("/api/QrScanLogin");
        excudeList.add("/api/CheckQrScanLogin");
        excudeList.add("/api/app/version");
        excudeList.add("/api/Users/saveoauth");
        excudeList.add("/api/OauthUser/appbind");
        excudeList.add("/api/esearch");
        excudeList.add("/api/HelpMsg");
        excudeList.add("/api/template/tsp");
        excudeList.add("/api/forecastTemplate/jspk");
        excudeList.add("/api/cjs/journal/security");
        excudeList.add("/api/cjs/journal/quality");
        excudeList.add("/api/cjs/journal/technical");
        excudeList.add("/api/tmms/report/download");
        excudeList.add("/api/tmms/points/download");
        excudeList.add("/api/cad");
        registry.addInterceptor(auth()).addPathPatterns("/**").excludePathPatterns(excudeList);
        System.out.println("auth started");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);//忽略大小写
        configurer.setPathMatcher(matcher);
    }

    //    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/");
//        super.addResourceHandlers(registry);
//    }
    @Bean
    public AuthorizeInterceptor auth() {
        return new AuthorizeInterceptor();
    }

    static {
        TypeUtils.compatibleWithJavaBean = true;
        TypeUtils.compatibleWithFieldName = true;//大写就大写，小写就小写
    }

    public HttpMessageConverter<Object> fastJsonHttpMessageConverter() {
        //TypeUtils.compatibleWithFieldName=true;//大写就大写，小写就小写
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,//关闭循环引用
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
     * 配饰新的对象json转换取代springboot默认的jackson
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ResourceRegionHttpMessageConverter());
        System.out.println("exchange to fastjson");
    }
}
