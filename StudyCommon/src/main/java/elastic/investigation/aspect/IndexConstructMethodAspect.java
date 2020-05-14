package elastic.investigation.aspect;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.service.IndexConstructMethodService;
import com.tunnelkey.tktim.model.Investigation.Structure.model.ConstructMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2018/12/20 14:28
 */
@Aspect
@Component
public class IndexConstructMethodAspect {
    @Autowired
    private IndexConstructMethodService service;

    @Pointcut("execution(public * com.tunnelkey.tktim.business.investigation.InterfaceImpl.structure.ConstructMethodServiceImpl.save(..))")
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
    @SuppressWarnings("unchecked")
    public void afterReturning(JoinPoint pjp, Object ret) {
        if (!ESCommon.isDataExists(pjp, ret)) return;
        if (ret instanceof Collection) {
            List<ConstructMethod> list = (ArrayList<ConstructMethod>) ret;
            list.forEach(model -> service.save(model));
        } else {
            ConstructMethod model = (ConstructMethod) ret;
            service.save(model);
        }
    }

    @Pointcut("execution(public * com.tunnelkey.tktim.business.investigation.InterfaceImpl.structure.ConstructMethodServiceImpl.delete(..))")
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
        if (ret instanceof Collection) {
            List<ConstructMethod> list = (ArrayList<ConstructMethod>) ret;
            list.forEach(model -> service.delete(model));
        } else {
            ConstructMethod model = (ConstructMethod) ret;
            service.delete(model);
        }
    }
}
