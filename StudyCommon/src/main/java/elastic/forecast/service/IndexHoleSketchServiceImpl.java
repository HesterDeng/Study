package elastic.forecast.service;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexHoleSketchRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.infrastructure.UnityHelper;
import com.tunnelkey.tktim.model.elastic.forecast.IndexHoleSketchDoc;
import com.tunnelkey.tktim.model.forecast.holeSketch.model.GeologyHoleSketchReportModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @Version: 0.0.1
 * @Description: 地质洞身素描的索引接口
 * @Author: lxt
 * @Date: 2019/2/16 15:16
 */
@Service
public class IndexHoleSketchServiceImpl {
    @Autowired
    @Lazy
    private IndexHoleSketchRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(GeologyHoleSketchReportModel model) {
        IndexHoleSketchDoc doc = new IndexHoleSketchDoc();
        esCommon.setSectionBaseInfo(doc, model.Id, model.Prefix, model.StartLegend, model.EndLegend, model.OperatorUserId, model.WorkSectionId);
        doc.setFaceLegend(model.FaceLegend);
        doc.setMonitorDate(model.MonitorDate);
        doc.setReportCode(model.ReportCode);
        doc.setLeftWallFaceFileName(model.LeftWallFaceFileName);
        doc.setLeftWallFaceFileAnalysisName(model.LeftWallFaceFileAnalysisName);
        doc.setRightWallFaceFileName(model.RightWallFaceFileName);
        doc.setRightWallFaceFileAnalysisName(model.RightWallFaceFileAnalysisName);
        doc.setTopFaceFileName(model.TopFaceFileName);
        doc.setTopFaceFileAnalysisName(model.TopFaceFileAnalysisName);
        doc.setWallRockGradeByDesign(model.WallRockGradeByDesign);
        doc.setWallRockGradeByReal(model.WallRockGradeByReal);
        doc.setSgdztz(model.sgdztz);
        doc.setSggztz(model.sggztz);
        doc.setShswtz(model.shswtz);
        doc.setSjdzms(model.sjdzms);
        doc.setConclusionyb(model.conclusionyb);
        doc.setSolution(model.solution);
        doc.setSuggestion(model.suggestion);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(GeologyHoleSketchReportModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     *
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexHoleSketchDoc doc) {
        String temp = "在里程范围{0}-{1},在{2}处洞身素描信息。预报日期：{12}，设计围岩级别：{3}，施工围岩级别：{4}，岩性特征：{5}，构造特征：{6}，水文地质特征：{7}，设计地质描述：{8}，预报结论：{9}；后续建议：{10}；实际采取措施{11}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getFaceLegend()),
                UnityHelper.luomaWY(Integer.parseInt(doc.getWallRockGradeByDesign())),
                UnityHelper.luomaWY(Integer.parseInt(doc.getWallRockGradeByReal())),
                doc.getSgdztz(),
                doc.getSggztz(),
                doc.getShswtz(),
                doc.getSjdzms(),
                doc.getConclusionyb(),
                doc.getSuggestion(),
                doc.getSolution(),
                esCommon.formatTime(doc.getMonitorDate())
        );
    }

}
