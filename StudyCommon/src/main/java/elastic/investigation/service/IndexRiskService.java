package elastic.investigation.service;

import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.repository.IndexRiskRepository;
import com.tunnelkey.tktim.model.Investigation.Structure.model.GeologyRiskAssessment;
import com.tunnelkey.tktim.model.elastic.investigation.IndexRiskDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @version:（1.0.0.0）
 * @Description: 风险索引仓库实现
 * @author: enoch
 * @date: 2018/12/18 17:22
 */
@Service
public class IndexRiskService {

    @Autowired
    @Lazy
    private IndexRiskRepository template;

    @Autowired
    private ESCommon esCommon;

    @Autowired
    private ICode code;

    public void save(GeologyRiskAssessment model) {
        IndexRiskDoc doc =new IndexRiskDoc();
        esCommon.setGalleryBaseInfo(doc, model.Id, model.Legend, model.StartLegend, model.EndLegend, model.OperatorUserId, model.ForeignId);
        String riskGrade = code.getCodeName("riskrank",model.RiskGrade);
        doc.setRiskName(model.RiskType);
        doc.setRiskGrade(model.RiskGrade);
        doc.setContent(this.toContent(doc, model.RiskType, riskGrade,model.Remark,model.Measure));
        template.save(doc);
    }

    public void delete(GeologyRiskAssessment model) {
        template.deleteById(model.Id);
    }

    private String toContent(IndexRiskDoc doc, String riskType,String riskGrade, String remark, String measure) {
        String temp = "在里程范围内{0}到{1},风险名称:{2}，风险范围：{3}，地质描述：{4}，采取的措施：{5}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                riskType,
                riskGrade,
                remark,
                measure
        );
    }
}
