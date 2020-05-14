package com.aop;

import com.alibaba.fastjson.JSON;
import com.tunnelkey.tktim.miniclient.log.ILogService;
import com.tunnelkey.tktim.model.base.BusinessValidationException;
import com.tunnelkey.tktim.model.log.CommunicationPackageModel;
import com.tunnelkey.tktim.model.log.ExceptionInfoModel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private LogDeaUtil logDeaUtil;
    /**
     * 及其子包
     */
    @Pointcut("!execution(public * com.tunnelkey.tktim.api.controller.base.FileController.*(..)) && execution(public * com.tunnelkey.tktim.api.controller..*.*(..))")
    public void webLog() {

    }

    @Before("webLog()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("reqTime", LocalDateTime.now());
        }
    }

    @AfterThrowing(value = "webLog()", throwing = "exp")
    //@Async  //Async 会导致获取不到request的attributes=null
    public void throwss(JoinPoint pjp, Throwable exp) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        ExceptionInfoModel model = new ExceptionInfoModel();
        //业务异常不记录日志
        if (exp.getClass().equals(BusinessValidationException.class))
            return;
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            HttpServletRequest request = attributes.getRequest();
            model.TypeName = exp.getClass().getTypeName();
            model.Message = exp.getMessage();
            model.Source = request.getRequestURI();
            model.StackTrace = exp.getStackTrace().toString();
            model.Code = response.getStatus();
            model.ErrorUrl = pjp.getTarget().getClass().getSimpleName();//定位到controller
            logDeaUtil.addException(model);
        }
    }

    // 后置通知
    @AfterReturning(value = "webLog()", returning = "ret")
    //@Async  //Async 会导致获取不到request的attributes=null
    public void afterReturning(JoinPoint pjp, Object ret) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        CommunicationPackageModel logModel = new CommunicationPackageModel();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            int code = response.getStatus();
            HttpServletRequest request = attributes.getRequest();
            logModel.ContentSize = String.valueOf(request.getContentLength());
            logModel.RequestTime = logModel.StartTime = (LocalDateTime) request.getAttribute("reqTime");
            logModel.EndTime = LocalDateTime.now();
            logModel.Controller = pjp.getTarget().getClass().getSimpleName();
            logModel.Method = pjp.getSignature().getName();
            //只保存 保存于删除的动作 现在系统编辑基本都是put
            if (logModel.Method.toLowerCase().equals("get") || logModel.Method.toLowerCase().equals("post"))
                return;
            logModel.IpAddress = this.getRemoteHost(request);
            logModel.Code = code;
            if (Optional.ofNullable(pjp.getArgs()).isPresent()) {
                if (pjp.getArgs().length == 1) {
                    logModel.RequestString = safeToJSONString(pjp.getArgs()[0]);
                } else {
                    logModel.RequestString =safeToJSONString(pjp.getArgs());
                }
            }
            logModel.RequestType = request.getMethod();
            logModel.ResponseString =safeToJSONString(ret);
            logDeaUtil.addLog(logModel);
        }
    }

    private String safeToJSONString(Object object) {
        String str = "";
        try {
            str = JSON.toJSONString(object);
        } catch (Exception e) {
        }
        return str;
    }

    /**
     * 获取目标主机的ip
     *
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    // 后置最终通知,都会执行无论异常
    @After(value = "webLog()")
    public void after(JoinPoint jp) {
//        System.out.println("方法最后执行.....");
    }
    // 环绕通知,环绕增强，相当于MethodInterceptor
    //抛出Throwable无法处理 故不采用此方法
//	@Around("webLog()")
//	public Object arround(ProceedingJoinPoint pjp) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        CommunicationPackageModel logModel = new CommunicationPackageModel();
//        if (attributes != null) {
//            HttpServletRequest request = attributes.getRequest();
//            logModel.ContentSize = String.valueOf(request.getContentLength());
//            logModel.EndTime = LocalDateTime.now();
//            logModel.RequestTime = logModel.StartTime = (LocalDateTime) request.getAttribute("reqTime");
//            logModel.Controller = pjp.getTarget().getClass().getSimpleName();
//            logModel.Method = pjp.getSignature().getName();
//            logModel.IpAddress = this.getRemoteHost(request);
//            if (Optional.ofNullable(pjp.getArgs()).isPresent()) {
//                if (pjp.getArgs().length == 1) {
//                    logModel.RequestString = JSON.toJSONString(pjp.getArgs()[0]);
//                } else {
//                    logModel.RequestString = JSON.toJSONString(pjp.getArgs());
//                }
//            }
//            logModel.RequestType = request.getMethod();
//        }
//		Object ret=null;
//		try {
//            ret = pjp.proceed();
//            logModel.ResponseString = JSON.toJSONString(ret);
//            logDeaUtil.addLog(logModel);
//			return ret;
//		} catch (Throwable e) {
//			return e;
//		}
//	}
}

@Service
class LogDeaUtil {
    @Autowired
    private ILogService iLogService;

    @Async
    public void addLog(CommunicationPackageModel logModel) {
        iLogService.add(logModel);
    }

    @Async
    public void addException(ExceptionInfoModel req) {
        iLogService.exception(req);
    }
}
