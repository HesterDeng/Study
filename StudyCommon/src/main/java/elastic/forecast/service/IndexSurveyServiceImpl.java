package elastic.forecast.service;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexSurveyRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.infrastructure.UnityHelper;
import com.tunnelkey.tktim.model.elastic.forecast.IndexSurveyDoc;
import com.tunnelkey.tktim.model.forecast.survey.model.GeologySurveyReportModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @Version: 0.0.1
 * @Description: 地质地表补充的索引接口
 * @Author: lxt
 * @Date: 2019/2/16 15:18
 */
@Service
public class IndexSurveyServiceImpl {
    @Autowired
    @Lazy
    private IndexSurveyRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(GeologySurveyReportModel model) {
        IndexSurveyDoc doc = new IndexSurveyDoc();
        esCommon.setSectionBaseInfo(doc, model.Id, model.Prefix, model.StartLegend, model.EndLegend, model.OperatorUserId, model.WorkSectionId);
        doc.setFaceLegend(model.FaceLegend);
        doc.setMonitorDate(model.MonitorDate);
        doc.setReportCode(model.ReportCode);
        doc.setDesignWallRockLevel(model.DesignWallRockLevel);
        doc.setDcyx(model.dcyx);
        doc.setDbry(model.dbry);
        doc.setTsdz(model.tsdz);
        doc.setRwdk(model.rwdk);
        doc.setRemark(model.remark);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(GeologySurveyReportModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     *
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexSurveyDoc doc) {
        String temp = "在里程范围{0}-{1},{2}处地表补充信息。设计围岩级别：{3}，预报日期：{4}，报告编号：{5}，" +
                "地层岩性：{6}；地表熔岩：{7}；特殊地质：{8}；人为导坑{9}；备注为：{10}。";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getFaceLegend()),
                UnityHelper.luomaWY(Integer.parseInt(doc.getDesignWallRockLevel())),
                esCommon.formatTime(doc.getMonitorDate()),
                doc.getReportCode(),
                doc.getDcyx(),
                doc.getDbry(),
                doc.getTsdz(),
                doc.getRwdk(),
                doc.getRemark()
        );
    }

}
