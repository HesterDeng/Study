package elastic.investigation.service;

import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.repository.IndexAGFRepository;
import com.tunnelkey.tktim.model.Investigation.Structure.model.AdvanceGeologyForecast;
import com.tunnelkey.tktim.model.elastic.investigation.IndexAGFDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * @version:（1.0.0.0）
 * @Description: 预报方法仓库实现
 * @author: enoch
 * @date: 2018/12/18 17:22
 */
@Service
public class IndexAGFService {
    @Autowired
    private ICode iCode;
    @Autowired
    @Lazy
    private IndexAGFRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(AdvanceGeologyForecast model) {
        IndexAGFDoc doc = new IndexAGFDoc();
        esCommon.setGalleryBaseInfo(doc, model.Id, model.Legend, model.StartLegend, model.EndLegend, model.OperatorUserId, model.ForeignId);

        String name = "";
        if (!StringUtils.isEmpty(model.ForecastGrade) && !StringUtils.isEmpty(model.ForecastMethod)) {
            name = this.getMethodName(model.ForecastGrade, model.ForecastMethod);
        }
        doc.setForecastMethodName(model.ForecastMethod);
        doc.setForecastGradeName(model.ForecastGrade);
        doc.setContent(this.toContent(doc, name));
        template.save(doc);
    }

    public void delete(AdvanceGeologyForecast model) {
        template.deleteById(model.Id);
    }

    private String toContent(IndexAGFDoc doc, String name) {
        String temp = "在里程范围内{0}到{1},设计预报方法是:{2}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                name
        );
    }

    private String getMethodName(String grade, String method) {
        String name = "";
        int gradeParse = Integer.parseInt(grade);
        switch (gradeParse) {
            case 1:
                name = iCode.getCodeName("GeologyMethod", method);
                break;
            case 2:
                name = iCode.getCodeName("DrillingMethod", method);
                break;
            case 3:
                name = iCode.getCodeName("GeophysicalMethod", method);
                break;
            case 4:
                name = iCode.getCodeName("PilotMethod", method);
                break;
            case 5:
                name = iCode.getCodeName("Comprehensive", method);
                break;
        }
        return name;
    }
}
