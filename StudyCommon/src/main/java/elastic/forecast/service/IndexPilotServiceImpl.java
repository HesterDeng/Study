package elastic.forecast.service;

import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexPilotRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.elastic.forecast.IndexPilotDoc;
import com.tunnelkey.tktim.model.forecast.GeologyForecastReport;
import com.tunnelkey.tktim.model.forecast.PilotAndReportModel;
import com.tunnelkey.tktim.model.forecast.pilot.model.PilotModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @Version: 0.0.1
 * @Description: 地质导坑的索引数据接口
 * @Author: lxt
 * @Date: 2019/2/16 15:17
 */
@Service
public class IndexPilotServiceImpl {
    @Autowired
    @Lazy
    private IndexPilotRepository template;

    @Autowired
    private ICode code;

    @Autowired
    private ESCommon esCommon;

    public void save(PilotAndReportModel reportModel) {
        PilotModel model = reportModel.PilotModel;
        GeologyForecastReport report = reportModel.GeologyForecastReport;
        IndexPilotDoc doc = new IndexPilotDoc();
        esCommon.setSectionBaseInfo(doc, model.Id, report.LegendPrefix, report.StartLegend, report.EndLegend, model.OperatorUserId, model.WorkSectionId);
        doc.setFaceLegend(model.FaceLegend);
        doc.setForecastTime(model.ForecastTime);
        doc.setRemark(model.Remark);
        doc.setPilotMethod(model.PilotMethod);

        doc.setReportCode(report.ReportCode);
        doc.setReportRemark(report.Remark);
        doc.setSuggestion(report.suggestion);
        doc.setSolution(report.solution);

        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(PilotModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     *
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexPilotDoc doc) {
        String temp = "预报范围{8}-{9}，在{0}处地质导坑信息。导坑方法：{1}，施作时间：{2}，备注：{3}，报告编号：{4}，后续建议：{5}，采取措施：{6}，报告备注：{7}。";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getFaceLegend()),
                code.getCodeName("PilotMethod",doc.getPilotMethod()),
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
