package elastic;

import com.tunnelkey.tktim.business.common.ProjectHelper;
import com.tunnelkey.tktim.business.investigation.IInterface.IMainTackGalleryService;
import com.tunnelkey.tktim.business.project.IUserInfoService;
import com.tunnelkey.tktim.model.Investigation.model.MainTackGallery;
import com.tunnelkey.tktim.model.elastic.SearchDocumentBaseInfo;
import com.tunnelkey.tktim.model.project.Tunnel.TunnelInfo;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

/**
* @Version: 0.0.1
* @Description: es相关的公共内容
* @Author: lxt
* @Date:  2019/2/16 16:40
*/
@Component
public class ESCommon {

    @Autowired
    private ProjectHelper projectHelper;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IMainTackGalleryService mainTackGalleryService;


    /**
     * 判断是否存在数据
     * @param pjp 切点
     * @param ret 返回值
     * @return 是否符合条件
     */
    public static boolean isDataExists(JoinPoint pjp, Object ret){
        Object[] args = pjp.getArgs();
        return (args != null && args.length >= 1) && (ret != null);
    }

    /**
     * 配置基础数据的结构
     * @param doc 需要配置的数据对象
     * @param id 主键id
     * @param legend 里程前缀
     * @param startLegend 开始里程
     * @param endLegend 结束里程
     * @param operatorUserId 操作人id
     * @param workSectionId 区间id
     */
    public void setSectionBaseInfo(SearchDocumentBaseInfo doc, UUID id,String legend, String startLegend, String endLegend,UUID operatorUserId, UUID workSectionId){
        setSearchDocumentBaseInfo(doc,id,legend,startLegend,endLegend,operatorUserId);
        if (workSectionId != null){
            TunnelInfo tunnelInfo = projectHelper.getTunnelByWorkSectionId(workSectionId);
            if (tunnelInfo != null){
                doc.setTunnelId(tunnelInfo.TunnelId);
            }
        }
    }

    /**
     * 配置基础数据的结构
     * @param doc 需要配置的数据对象
     * @param id 主键id
     * @param legend 里程前缀
     * @param startLegend 开始里程
     * @param endLegend 结束里程
     * @param operatorUserId 操作人id
     * @param galleryId 正线辅助坑道id
     */
    public void setGalleryBaseInfo(SearchDocumentBaseInfo doc, UUID id,String legend, String startLegend, String endLegend,UUID operatorUserId, UUID galleryId){
        setSearchDocumentBaseInfo(doc,id,legend,startLegend,endLegend,operatorUserId);
        if (galleryId != null){
            MainTackGallery mainTackGallery = mainTackGalleryService.get(galleryId);
            if (mainTackGallery != null) {
                doc.setTunnelId(mainTackGallery.ForeignId);
            }
        }
    }

    /**
     * 配置基础数据的结构
     * @param doc 需要配置的数据对象
     * @param id 主键id
     * @param legend 里程前缀
     * @param startLegend 开始里程
     * @param endLegend 结束里程
     * @param operatorUserId 操作人id
     */
    private void setSearchDocumentBaseInfo(SearchDocumentBaseInfo doc, UUID id,String legend, String startLegend, String endLegend,UUID operatorUserId){
        doc.setKeyId(id);
        doc.setCreateTime(LocalDateTime.now());
        doc.setStartLegend(Double.parseDouble( "".equals(startLegend)? "0" : startLegend));
        doc.setLegend(legend);
        doc.setEndLegend(Double.parseDouble("".equals(endLegend)? "0" : endLegend));
        doc.setCreateUserId(operatorUserId);
        doc.setCreateUserName(userInfoService.findUsersByIds(Collections.singletonList(operatorUserId)).get(0).UserName);
    }

    /**
     * 格式化时间为 年月日
     * @param time 需要格式化的时间
     * @return 格式化后的数据
     */
    public String formatTime(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        return formatter.format(time);
    }
}
