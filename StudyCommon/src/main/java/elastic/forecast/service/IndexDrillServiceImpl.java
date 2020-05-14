package elastic.forecast.service;

import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexDrillRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.elastic.forecast.IndexDrillDoc;
import com.tunnelkey.tktim.model.forecast.drill.model.GeologyDrillModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
* @Version: 0.0.1
* @Description: 地质钻探的索引接口
* @Author: lxt
* @Date:  2019/2/16 15:12
*/
@Service
public class IndexDrillServiceImpl {
    @Autowired
    @Lazy
    private IndexDrillRepository template;

    @Autowired
    private ESCommon esCommon;

    @Autowired
    private ICode code;

    public void save(GeologyDrillModel model){
        IndexDrillDoc doc = new IndexDrillDoc();
        esCommon.setSectionBaseInfo(doc,model.Id,model.Prefix,model.StartLegend,model.EndLegend,model.OperatorUserId,model.WorkSectionId);
        doc.setFaceLegend(model.FaceLegend);
        doc.setDrillingMethod(model.DrillingMethod);
        doc.setMonitorDate(model.MonitorDate);
        doc.setReportCode(model.ReportCode);
        doc.setConclusionyb(model.Conclusionyb);
        doc.setSolution(model.Solution);
        doc.setSuggestion(model.Suggestion);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(GeologyDrillModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexDrillDoc doc){
        String temp = "在里程范围{0}-{1}，掌子面{2}信息,钻探方法：{3}，预报时间：{8}，报告编号：{4}，预报结论：{5}，实际采取的措施：{6}，后续建议：{7}。";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getEndLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getFaceLegend()),
                code.getCodeName("DrillingMethod",doc.getDrillingMethod()),
                doc.getReportCode(),
                doc.getConclusionyb(),
                doc.getSolution(),
                doc.getSuggestion(),
                esCommon.formatTime(doc.getMonitorDate())
        );
    }
}
