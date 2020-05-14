package elastic.investigation.service;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.investigation.repository.IndexWallRockRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.infrastructure.UnityHelper;
import com.tunnelkey.tktim.model.Investigation.Structure.model.WallRockClassification;
import com.tunnelkey.tktim.model.elastic.investigation.IndexWallRockDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @version:（1.0.0.0）
 * @Description:
 * @author: enoch
 * @date: 2018/12/18 17:22
 */
@Service
public class IndexWallRockService{

    @Autowired
    @Lazy
    private IndexWallRockRepository template;


    @Autowired
    private ESCommon esCommon;

    public void save(WallRockClassification model) {
        IndexWallRockDoc doc = new IndexWallRockDoc();
        esCommon.setGalleryBaseInfo(doc, model.Id, model.Legend, model.StartLegend, model.EndLegend, model.OperatorUserId, model.ForeignId);
        doc.setWallRockGrade(model.GrandCode);
        doc.setWallRockType(model.WallRockType);
        doc.setGeohydrology(model.Geohydrology);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(WallRockClassification model) {
        template.deleteById(model.Id);
    }

    private String toContent(IndexWallRockDoc doc) {
        String temp = "在里程范围内{0}到{1},围岩等级是{2},岩体类型是{3},地质情况是{4}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                UnityHelper.luomaWY(Integer.parseInt(doc.getWallRockGrade())),
                doc.getWallRockType(),
                doc.getGeohydrology()
        );
    }
}
