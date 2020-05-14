package com.aop;

import com.tunnelkey.tktim.api.exception.NoAuthException;
import com.tunnelkey.tktim.api.util.JWTUtil;
import com.tunnelkey.tktim.business.project.IUserInfoService;
import com.tunnelkey.tktim.infrastructure.UUIDHelper;
import com.tunnelkey.tktim.model.Company.UserRelationCompany;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: 刘毅
 * @date: 2019/3/22 11:24
 */
@Aspect
@Component
public class CompanyAdminAspect {

    @Autowired
    private IUserInfoService iUserInfoService;


    @Pointcut("@annotation(com.tunnelkey.tktim.api.annotation.CompanyAdmin)")
    public void comCut() {
    }

    @Before("comCut()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            UUID userId = null, companyId = null;
            if (!StringUtils.isEmpty(token)) {
                Claims chaim = JWTUtil.parseJWT(token);
                if (Optional.ofNullable(chaim.get("CompanyId")).isPresent()) {
                    companyId = UUID.fromString(chaim.get("CompanyId").toString());
                } else {
                    throw new NoAuthException();
                }
                if (Optional.ofNullable(chaim.get("UserId")).isPresent()) {
                    userId = UUID.fromString(chaim.get("UserId").toString());
                } else {
                    throw new NoAuthException();
                }
                if (!UUIDHelper.isEmpty(userId) && !UUIDHelper.isEmpty(companyId)) {
                    UserRelationCompany userRelationCompany = iUserInfoService.getUserRelationCompany(userId, companyId);
                    if (!ObjectUtils.isEmpty(userRelationCompany)) {
                        if (!userRelationCompany.IsMaster) {
                            throw new NoAuthException();
                        }
                    } else {
                        throw new NoAuthException();
                    }
                } else {
                    throw new NoAuthException();
                }
            } else {
                throw new NoAuthException();
            }
        }
    }
}
