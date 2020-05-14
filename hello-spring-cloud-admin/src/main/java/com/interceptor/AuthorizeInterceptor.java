package com.interceptor;

import com.alibaba.fastjson.JSON;
import com.tunnelkey.tktim.api.exception.NoAuthException;
import com.tunnelkey.tktim.api.model.BaseExceptionModel;
import com.tunnelkey.tktim.api.model.BaseResponse;
import com.tunnelkey.tktim.api.model.ResponseHeader;
import com.tunnelkey.tktim.api.util.JWTUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @version:（1.0.0.0）
 * @Description: （访问权限拦截类）
 * @author: enoch
 * @date: 2018年11月6日 下午1:57:53
 */
public class AuthorizeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                             Object object) throws Exception {
        if (httpServletRequest.getMethod().toLowerCase().equals("options"))
            return true;
        String token = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isEmpty(StringUtils.trimWhitespace(token))) {
            token = httpServletRequest.getParameter("Authorization");
        }
        Optional<Object> cert = Optional.ofNullable(httpServletRequest.getAttribute("javax.servlet.request.X509Certificate"));
        if (StringUtils.isEmpty(token) && !cert.isPresent()) {
            this.noHeader(httpServletResponse);
            return false;
        }
        if (cert.isPresent()){
            return true;
        }
        // 过期判断
        JWTUtil.parseJWT(token);
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        return !method.isAnnotationPresent(AuthAnnotation.class);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) {
//        System.out.println("interceptor httpStatus" + httpServletResponse.getStatus());
//        System.out.println("FirstInterceptor postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) {
//        System.out.println("FirstInterceptor object" + o);
//        System.out.println("FirstInterceptor exception" + e);
//        System.out.println("FirstInterceptor afterCompletion");
    }

    private void noHeader(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
        //无访问权限跨域访问配置，避免无法获取返回值
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Max-Age", "1800");
        PrintWriter writer = httpServletResponse.getWriter();
        BaseResponse<BaseExceptionModel> res = new BaseResponse<BaseExceptionModel>();
        NoAuthException noAuthException = new NoAuthException();
        ResponseHeader header = new ResponseHeader();
        header.Success = false;
        header.Code = noAuthException.getCode();
        header.Message = noAuthException.getMessage();
        BaseExceptionModel body = new BaseExceptionModel();
        body.exceptionType = noAuthException.getMessage();
        res.Header = header;
        res.Body = body;
        writer.write(JSON.toJSONString(res));
    }
}
