package com.util;

import com.alibaba.fastjson.JSON;
import com.tunnelkey.tktim.api.model.BaseExceptionModel;
import com.tunnelkey.tktim.api.model.BaseResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: 刘毅
 * @date: 2019/4/26 9:30
 */
public class HttpResponseUtils {

    public static void response(HttpServletResponse response, BaseResponse<BaseExceptionModel> res) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        //登录失效之后跨域访问配置，避免无法获取返回值
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Max-Age", "1800");
        String json = JSON.toJSONString(res);
        int code = res.Header.Code;
        response.setStatus(code);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(json);
        printWriter.flush();
    }

    /**
     * 获取下载
     *
     * @param is       文件流
     * @param fileName 文件名
     * @param type     文件后缀名
     * @return 结果
     */
    public static ResponseEntity getDownloadEntity(InputStream is, @Nullable String fileName, @Nullable String type) {
        return ResponseEntity
                .ok()
                .headers(getHeaders(fileName + "." + type, true))
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(is));
    }

    /**
     * 获取文件不进行下载
     *
     * @param is       文件流
     * @param fileName 文件名
     * @return 结果
     */
    public static ResponseEntity getTransformEntity(InputStream is, @Nullable String fileName) {
        return ResponseEntity
                .ok()
                .headers(getHeaders(fileName, false))
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(is));
    }

    /**
     * 获取headers
     *
     * @param fileName   文件名称
     * @param isDownload 是否下载
     * @return 返回值
     */
    private static HttpHeaders getHeaders(String fileName, boolean isDownload) {
        if (fileName == null || fileName.length() == 0) {
            fileName = UUID.randomUUID().toString();
        }
        String contentDisposition;
        if (isDownload) {
            contentDisposition = String.format("attachment;filename=\"%s\"", new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
        } else {
            contentDisposition = String.format("inline;filename=\"%s\"", new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", contentDisposition);
        return headers;
    }
}
