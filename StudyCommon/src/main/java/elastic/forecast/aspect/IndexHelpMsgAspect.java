package elastic.forecast.aspect;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.service.IndexHelpMsgServiceImpl;
import com.tunnelkey.tktim.infrastructure.UUIDHelper;
import com.tunnelkey.tktim.model.request.DeleteRequest;
import com.tunnelkey.tktim.model.system.HelpMsgModel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @Auther: dengguiping
 * @Date: 2019/2/26 16:21
 * @Description:
 */
@Aspect
@Component
public class IndexHelpMsgAspect {
    @Autowired
    private IndexHelpMsgServiceImpl service;

    @Pointcut("execution(public * com.tunnelkey.tktim.business.system.impl.HelpMsgImpl.editHelpMsg(..))")
    public void saveAspect() {
    }

    /**
     * 写索引
     *
     * @param pjp 切点
     * @param ret 对象
     */
    @AfterReturning(value = "saveAspect()", returning = "ret")
    @Async
    public void afterReturning(JoinPoint pjp, Object ret) {
        if (!ESCommon.isDataExists(pjp, ret)) return;
        Object[] args = pjp.getArgs();
        if (ObjectUtils.isEmpty(args))
            return;
        if (args[0] instanceof HelpMsgModel) {
            HelpMsgModel model = (HelpMsgModel) args[0];
            service.save(model);
        }

    }

    @Pointcut("execution(public * com.tunnelkey.tktim.business.system.impl.HelpMsgImpl.deleteHelpMsg(..))")
    public void deleteAspect() {
    }

    /**
     * 删除索引
     *
     * @param pjp 切点
     * @param ret 对象
     */
    @AfterReturning(value = "deleteAspect()", returning = "ret")
    @Async
    @SuppressWarnings("unchecked")
    public void afterDeleteReturning(JoinPoint pjp, Object ret) {
        if (!ESCommon.isDataExists(pjp, ret)) return;
        Object[] args = pjp.getArgs();
        if (ObjectUtils.isEmpty(args))
            return;
        if (args[0] instanceof DeleteRequest) {
            DeleteRequest request = (DeleteRequest) args[0];
            if (!UUIDHelper.isEmpty(request.KeyId)) {
                service.delete(request.KeyId);
            }
        }
    }
}
