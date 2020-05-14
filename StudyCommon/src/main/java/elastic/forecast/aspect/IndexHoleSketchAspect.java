package elastic.forecast.aspect;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.service.IndexHoleSketchServiceImpl;
import com.tunnelkey.tktim.model.forecast.holeSketch.model.GeologyHoleSketchReportModel;
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
* @Description: 地质洞身素描的索引切面
* @Author: lxt
* @Date:  2019/2/18 11:28
*/
@Aspect
@Component
public class IndexHoleSketchAspect {
    @Autowired
    private IndexHoleSketchServiceImpl service;

    @Pointcut("execution(public * com.tunnelkey.tktim.business.forecast.InterfaceImpl.HoleSketchServiceImpl.save(..))")
    public void saveAspect() {
    }

    /**
     * 写索引
     * @param pjp 切点
     * @param ret 对象
     */
    @AfterReturning(value = "saveAspect()", returning = "ret")
    @Async
    public void afterReturning(JoinPoint pjp, Object ret) {
        if ( !ESCommon.isDataExists(pjp, ret) ) return;
        GeologyHoleSketchReportModel model = (GeologyHoleSketchReportModel) ret;
        service.save(model);
    }

    @Pointcut("execution(public * com.tunnelkey.tktim.business.forecast.InterfaceImpl.HoleSketchServiceImpl.delete(..))")
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
            List<GeologyHoleSketchReportModel> list = (ArrayList<GeologyHoleSketchReportModel>) ret;
            list.forEach(model -> service.delete(model));
        } else {
            GeologyHoleSketchReportModel model = (GeologyHoleSketchReportModel) ret;
            service.delete(model);
        }
    }
}
