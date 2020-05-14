package elastic.forecast.service;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexFaceRepository;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.elastic.forecast.IndexFaceDoc;
import com.tunnelkey.tktim.model.forecast.face.TunnelFaceNewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
* @Version: 0.0.1
* @Description: 地质掌子面的索引接口
* @Author: lxt
* @Date:  2019/2/16 15:14
*/
@Service
public class IndexFaceServiceImpl {
    @Autowired
    @Lazy
    private IndexFaceRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(TunnelFaceNewModel model){
        IndexFaceDoc doc = new IndexFaceDoc();
        esCommon.setSectionBaseInfo(doc,model.Id,"","","",model.OperatorUserId,model.WorkSectionId);
        doc.setLegend(model.Legend);
        doc.setRemark(model.Remark);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(TunnelFaceNewModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexFaceDoc doc){
        String temp = "在{2}处掌子面信息。掌子面描述：{3}";
        return MessageFormat.format(
                temp,
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getStartLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(), doc.getEndLegend()),
                LegendUtils.getFormatLegendWithPrefix(doc.getLegend(),doc.getLegend()),
                doc.getRemark()
        );
    }

}
