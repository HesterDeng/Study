package elastic.forecast.service;

import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexGeophysicalRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.elastic.forecast.IndexGeophysicalDoc;
import com.tunnelkey.tktim.model.forecast.GeologyForecastReport;
import com.tunnelkey.tktim.model.forecast.geophysical.model.GeophysicalAndReportModel;
import com.tunnelkey.tktim.model.forecast.geophysical.model.GeophysicalModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
* @Version: 0.0.1
* @Description: 地质物探的索引接口
* @Author: lxt
* @Date:  2019/2/16 15:15
*/
@Service
public class IndexGeophysicalServiceImpl {
    @Autowired
    @Lazy
    private IndexGeophysicalRepository template;

    @Autowired
    private ICode code;

    @Autowired
    private ESCommon esCommon;

    public void save(GeophysicalAndReportModel model){
        IndexGeophysicalDoc doc = new IndexGeophysicalDoc();
        GeophysicalModel tempModel = model.GeophysicalModel;
        GeologyForecastReport tempReport = model.GeologyForecastReport;
        esCommon.setSectionBaseInfo(doc,tempModel.Id,tempReport.LegendPrefix,tempReport.StartLegend,tempReport.EndLegend,tempModel.OperatorUserId,tempModel.WorkSectionId);
        doc.setFaceLegend(tempModel.FaceLegend);
        doc.setForecastTime(tempModel.ForecastTime);
        doc.setRemark(tempModel.Remark);
        doc.setForecastMethod(tempModel.ForecastMethod);

        doc.setReportCode(tempReport.ReportCode);
        doc.setReportRemark(tempReport.Remark);
        doc.setSolution(tempReport.solution);
        doc.setSuggestion(tempReport.suggestion);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(GeophysicalModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexGeophysicalDoc doc){
        String temp = "预报范围{8}-{9}，在{0}处地质物探信息。物探方法：{1}，施作时间：{2}，备注：{3}，报告编号：{4}，后续建议：{5}，采取措施：{6}，报告备注：{7}。";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getFaceLegend()),
                code.getCodeName("GeophysicalMethod",doc.getForecastMethod()),
                esCommon.formatTime(doc.getForecastTime()),
                doc.getRemark(),
                doc.getReportCode(),
                doc.getSuggestion(),
                doc.getSolution(),
                doc.getReportRemark(),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getEndLegend())
        );
    }

}
