package elastic.investigation.service;

import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.repository.IndexUnfavorableRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.Investigation.Structure.model.UnfavorableGeologyModel;
import com.tunnelkey.tktim.model.elastic.investigation.IndexUnfavorableRiskDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @version:（1.0.0.0）
 * @Description: 不良地质索引仓库实现
 * @author: enoch
 * @date: 2018/12/18 17:22
 */
@Service
public class IndexUnfavorableService {

    @Autowired
    private ICode code;

    @Autowired
    @Lazy
    private IndexUnfavorableRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(UnfavorableGeologyModel model) {
        IndexUnfavorableRiskDoc doc = new IndexUnfavorableRiskDoc();
        esCommon.setGalleryBaseInfo(doc, model.Id, model.Legend, model.StartLegend, model.EndLegend, model.OperatorUserId, model.ForeignId);
        String riskName = code.getCodeName("GeologyClassify", model.RiskType);
        String riskGrade = code.getCodeName("riskrank",model.RiskGrade);
        doc.setRiskName(model.RiskType);
        doc.setRiskGrade(model.RiskGrade);
        doc.setContent(this.toContent(doc, riskName,riskGrade, model.Remark, model.Measure));
        template.save(doc);
    }

    public void delete(UnfavorableGeologyModel model) {
        template.deleteById(model.Id);
    }

    private String toContent(IndexUnfavorableRiskDoc doc, String riskName,String riskGrade , String remark, String measure) {
        String temp = "在里程范围内{0}到{1},不良地质类型:{2}，复杂程度分级：{3}，地质描述：{4}，采取的措施：{5}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                riskName,
                riskGrade,
                remark,
                measure
        );
    }
}
