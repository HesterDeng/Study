package elastic.forecast.aspect;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.service.IndexGeophysicalServiceImpl;
import com.tunnelkey.tktim.model.forecast.geophysical.model.GeophysicalAndReportModel;
import com.tunnelkey.tktim.model.forecast.geophysical.model.GeophysicalModel;
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
 * @Version: 0.0.1
 * @Description: 地质物探的索引切面
 * @Author: lxt
 * @Date: 2019/2/18 11:27
 */
@Aspect
@Component
public class IndexGeophysicalAspect {

    @Autowired
    private IndexGeophysicalServiceImpl service;

    @Pointcut("execution(public * com.tunnelkey.tktim.business.forecast.InterfaceImpl.GeophysicalReportServiceImpl.save(..))")
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
        GeophysicalAndReportModel model = (GeophysicalAndReportModel) ret;
        service.save(model);
    }

    @Pointcut("execution(public * com.tunnelkey.tktim.business.forecast.InterfaceImpl.GeophysicalReportServiceImpl.delete(..))")
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
            List<GeophysicalModel> list = (ArrayList<GeophysicalModel>) ret;
            list.forEach(model -> service.delete(model));
        } else {
            GeophysicalModel model = (GeophysicalModel) ret;
            service.delete(model);
        }
    }
}
