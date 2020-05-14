package elastic.investigation.service;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.repository.IndexLiningRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.Investigation.Structure.model.LiningType;
import com.tunnelkey.tktim.model.elastic.investigation.IndexLiningDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @version:（1.0.0.0）
 * @Description: 衬砌类型仓库实现
 * @author: enoch
 * @date: 2018/12/18 17:22
 */
@Service
public class IndexLiningService {

    @Autowired
    @Lazy
    private IndexLiningRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(LiningType model) {
        IndexLiningDoc doc = new IndexLiningDoc();
        esCommon.setGalleryBaseInfo(doc, model.Id, model.Legend, model.StartLegend, model.EndLegend, model.OperatorUserId, model.ForeignId);
        String name = model.LiningTypeCode;
        doc.setLiningName(name);
        doc.setContent(this.toContent(doc, name));
       template.save(doc);
    }

    public void delete(LiningType model) {
        template.deleteById(model.Id);
    }

    private String toContent(IndexLiningDoc doc, String name) {
        String temp = "在里程范围内{0}到{1},衬砌类型是:{2}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                name
        );
    }
}
