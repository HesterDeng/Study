package com.aop;

import com.tunnelkey.tktim.api.exception.ValidatorException;
import com.tunnelkey.tktim.api.model.BaseExceptionModel;
import com.tunnelkey.tktim.api.model.BaseRequest;
import com.tunnelkey.tktim.api.model.BaseResponse;
import com.tunnelkey.tktim.api.model.ResponseHeader;
import com.tunnelkey.tktim.api.util.HttpResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 统一数据验证
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class ValidatorAspect {

    private Validator validator;

    @Pointcut("execution(public * com.tunnelkey.tktim.api.controller..*.*(..))")
    public void pointcut() {
    }

    /**
     * 入参校验
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];//参数的个数，而不是属性
            if (!ObjectUtils.isEmpty(arg) && isvalid(arg)) {
                Set<ConstraintViolation<Object>> constraintViolations = validator.validate(arg);
                if (constraintViolations.size() > 0) {
                    for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
                        Path property = constraintViolation.getPropertyPath();
                        String name = property.iterator().next().getName();
                        sb.append("[" + name + "]" + constraintViolation.getMessage());
                    }
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    HttpServletResponse response = attributes.getResponse();
                    BaseResponse<BaseExceptionModel> res = new BaseResponse<BaseExceptionModel>();
                    ValidatorException validatorException = new ValidatorException();
                    validatorException.setMessage(sb.toString());
                    ResponseHeader header = new ResponseHeader();
                    header.Success = false;
                    header.Code = validatorException.getCode();
                    header.Message = validatorException.getMessage();
                    BaseExceptionModel body = new BaseExceptionModel();
                    body.exceptionType = validatorException.getMessage();
                    res.Header = header;
                    res.Body = body;
                    HttpResponseUtils.response(response, res);
                    return null;
                }
            }
        }
        return joinPoint.proceed();
    }

    private boolean isvalid(Object obj) {
        boolean valid = true;
        if (obj instanceof BaseRequest) {
            try {
                Field sv = obj.getClass().getField("sv");
                Boolean aBoolean = (Boolean) sv.get(obj);
                valid = aBoolean;
            } catch (Exception e) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }
}
