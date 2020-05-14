package com.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tunnelkey.tktim.api.annotation.UserActionFilter;
import com.tunnelkey.tktim.api.model.UserIdentity;
import com.tunnelkey.tktim.api.util.UserIdentityUtils;
import com.tunnelkey.tktim.miniclient.log.ILogService;
import com.tunnelkey.tktim.model.log.UserActionModel;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （用户重要操作日志记录，规避用户不承认数据的操作性,UI体现在IM）
 * @author: 刘毅
 * @date: 2019/3/22 11:24
 */
@Aspect
@Component
public class UserActionAspect {

    @Autowired
    private ILogService iLogService;

    @Pointcut("@annotation(com.tunnelkey.tktim.api.annotation.UserActionFilter)")
    public void actionCut() {
    }

    @AfterReturning(value = "actionCut()", returning = "ret")
    public void afterReturning(JoinPoint pjp, Object ret) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            this.addLog(attributes, pjp, ret);
        }
    }
    @Async
    public void addLog(ServletRequestAttributes attributes, JoinPoint pjp, Object ret) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        UserActionFilter actionFilter = method.getAnnotation(UserActionFilter.class);
        UserActionModel model = new UserActionModel();
        model.Module = actionFilter.m().ordinal();
        model.ActionName = actionFilter.a();
        model.ActionType = actionFilter.t().ordinal();
        model.ViewFunctionCode = actionFilter.v();
        model.RequestTime = LocalDateTime.now();
        //<editor-fold desc="获取头报文参数">
        String tid = attributes.getRequest().getHeader("TID");
        if (StringUtils.isNotEmpty(tid)) {
            UUID tunnelId = UUID.fromString(tid);
            model.TunnelId = tunnelId;
        }
        String lid = attributes.getRequest().getHeader("LID");
        if (StringUtils.isNotEmpty(lid)) {
            UUID tunnelId = UUID.fromString(lid);
            model.LineId = tunnelId;
        }
        String sid = attributes.getRequest().getHeader("SID");
        if (StringUtils.isNotEmpty(sid)) {
            UUID tunnelId = UUID.fromString(sid);
            model.SectionId = tunnelId;
        }
        String token = attributes.getRequest().getHeader("Authorization");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(token)) {
            UserIdentity parse = UserIdentityUtils.parse(token);
            if (!ObjectUtils.isEmpty(parse)) {
                model.UserId = parse.UserId;
            }
        }
        //</editor-fold>
        //参数名与参数值一一对应
        Object[] args = pjp.getArgs(); // 参数值
        if (StringUtils.isNotEmpty(actionFilter.k())) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String s = JSON.toJSONString(arg);
                JSONObject jsonObject = JSON.parseObject(s);
                Object o = jsonObject.get(actionFilter.k());
                if (!ObjectUtils.isEmpty(o)) {
                    model.KeyId = o.toString();
                    break;
                }
            }
        }
        if (!ObjectUtils.isEmpty(ret)) {
            String s = JSON.toJSONString(ret);
            JSONObject jsonObject = JSON.parseObject(s);
            Object o = jsonObject.get(actionFilter.outk());
            if (!ObjectUtils.isEmpty(o)) {
                model.OutKeyId = o.toString();
            }
        }
        String[] argNames = ((MethodSignature) pjp.getSignature()).getParameterNames(); // 参数名
        if (ObjectUtils.isEmpty(model) && ObjectUtils.isEmpty(model.UserId)) {
            iLogService.action(model);
        }
    }
}

