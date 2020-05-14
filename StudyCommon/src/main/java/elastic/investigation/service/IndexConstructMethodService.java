package elastic.investigation.service;

import com.tunnelkey.tktim.business.base.Interface.ICode;
import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.repository.IndexConstructMethodRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.Investigation.Structure.model.ConstructMethod;
import com.tunnelkey.tktim.model.elastic.investigation.IndexConstructMethodDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * @version:（1.0.0.0）
 * @Description: 施工方法仓库实现
 * @author: enoch
 * @date: 2018/12/18 17:22
 */
@Service
public class IndexConstructMethodService {
    @Autowired
    private ICode iCode;
    @Autowired
    @Lazy
    private IndexConstructMethodRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(ConstructMethod model) {
        IndexConstructMethodDoc doc = new IndexConstructMethodDoc();
        esCommon.setGalleryBaseInfo(doc, model.Id, model.Legend, model.StartLegend, model.EndLegend, model.OperatorUserId, model.ForeignId);

        String name = "", kwName = "";
        if (!StringUtils.isEmpty(model.MethodCode)) {
            name = iCode.getCodeName("ConstructionMethod", model.MethodCode);
            doc.setMethodName(name);
        }

        if (!StringUtils.isEmpty(model.ConstructStyle)) {
            kwName = iCode.getCodeName("ConstructStyle", model.ConstructStyle);
            doc.setKsName(kwName);
        }

        doc.setContent(this.toContent(doc, name, kwName));
        template.save(doc);
    }

    public void delete(ConstructMethod model) {
        template.deleteById(model.Id);
    }

    private String toContent(IndexConstructMethodDoc doc, String name, String kwName) {
        String temp = "在里程范围内{0}到{1},施工方法是:{2}，开挖方式是:{3}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                name,
                kwName
        );
    }
}
