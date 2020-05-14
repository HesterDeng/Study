package com.aop;

import com.tunnelkey.tktim.api.exception.NoAuthException;
import com.tunnelkey.tktim.api.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: 刘毅
 * @date: 2019/3/22 11:24
 */
@Aspect
@Component
public class SysAdminAspect {

    @Pointcut("@annotation(com.tunnelkey.tktim.api.annotation.SysAdmin)")
    public void adminCut() {
    }

    @Before("adminCut()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (!StringUtils.isEmpty(token)) {
                Claims chaim = JWTUtil.parseJWT(token);
                int role = Integer.parseInt(chaim.get("Role").toString());
                if (role != 1) {
                    throw new NoAuthException();
                }
            } else {
                throw new NoAuthException();
            }
        }
    }
}
