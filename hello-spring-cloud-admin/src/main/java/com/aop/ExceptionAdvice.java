package com.aop;

import com.tunnelkey.tktim.api.exception.BaseException;
import com.tunnelkey.tktim.api.exception.CloudException;
import com.tunnelkey.tktim.api.exception.ExpiredException;
import com.tunnelkey.tktim.api.model.BaseExceptionModel;
import com.tunnelkey.tktim.api.model.BaseResponse;
import com.tunnelkey.tktim.api.model.ResponseHeader;
import com.tunnelkey.tktim.api.util.HttpResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version:（1.0.0.0） * @Description: 异常统一处理类
 * * @author: enoch
 * * @date: 2018年11月2日 下午1:51:08
 */
@ControllerAdvice
@Component
public class ExceptionAdvice {
    @Autowired
    private org.slf4j.Logger logger;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public void defaultErrorHandler(HttpServletRequest req, HttpServletResponse response, Exception e) throws Exception {
        Integer statusCode = response.getStatus();
        ExceptionParam param = new ExceptionParam();
        param.code = HttpStatus.EXPECTATION_FAILED.value();
        param.msg = e.getMessage();
        System.out.println("---------------exception---------" + e);
        BaseResponse<BaseExceptionModel> res = new BaseResponse<BaseExceptionModel>();
        BaseExceptionModel exception = new BaseExceptionModel();
        jsonwebtoken(e, param);
        self(e, param);
        cloud(e, param);
        e.printStackTrace();
        exception.setExceptionType(param.msg);
        ResponseHeader header = new ResponseHeader(param.code, false);
        header.Message = param.msg;
        res.Header = header;
        res.Body = exception;
        HttpResponseUtils.response(response, res);
    }

    /**
     * 自定义异常chuli
     *
     * @param e
     */
    public void self(Exception e, ExceptionParam param) {
        if (e instanceof BaseException) {
            BaseException etwo = (BaseException) e;
            param.code = etwo.getCode();
            param.msg = etwo.getMessage();
        }
    }

    /**
     * 用户认证异常处理
     *
     * @param e
     */
    public void jsonwebtoken(Exception e, ExceptionParam param) {
        //当异常为失效异常或者token异常
        if (e instanceof io.jsonwebtoken.ExpiredJwtException || e instanceof io.jsonwebtoken.MalformedJwtException) {
            ExpiredException exp = new ExpiredException();
            param.code = exp.getCode();
            param.msg = exp.getMessage();
        }
    }

    /**
     * 微服务异常处理
     *
     * @param e
     */
    public void cloud(Exception e, ExceptionParam param) {
        if (e.getMessage().contains("com.netflix.client.ClientException")) {
            CloudException exp = new CloudException();
            param.code = exp.getCode();
            param.msg = exp.getMessage() + e.getMessage();
        }
    }


    class ExceptionParam {
        public int code;
        public String msg;
    }
}
