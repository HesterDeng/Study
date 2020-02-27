package com.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//路由登陆过滤器
@Component
public class LoginFilter extends ZuulFilter {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(LoginFilter.class);
    /**
     * 配置过滤类型，有四种不同生命周期的过滤器类型
     * 1. pre：路由之前
     * 2. routing：路由之时
     * 3. post：路由之后
     * 4. error：发送错误调用
     * @return
     */
    @Override
    public String filterType() {
        //再路由发起请求之前
        return "pre";
    }

    /**
     * 配置过滤的顺序
     * 数值越小执行越靠前
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 配置是否需要过滤：true/需要，false/不需要
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的具体业务代码
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();    //得到当前请求上下文
        HttpServletRequest request = context.getRequest();
        logger.info("{} >>> {}", request.getMethod(), request.getRequestURL().toString());
        String token = request.getParameter("token");       //得到参数
        if (token == null) {
            logger.warn("Token is empty");
            context.setSendZuulResponse(false);     //禁止发送zuul路由请求
            context.setResponseStatusCode(401);     //响应请求
            try {
                HttpServletResponse response=context.getResponse();
                response.setContentType("text/html;charset=utf-8"); //设置响应编码格式
                context.getResponse().getWriter().write("Token is empty");  //提示消息
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.info("OK");
        }
        return null;
    }
}
