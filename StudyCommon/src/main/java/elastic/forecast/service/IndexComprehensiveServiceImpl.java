package elastic.forecast.service;

import com.tunnelkey.tktim.business.elastic.ESCommon;
import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexComprehensiveRepository;
import com.tunnelkey.tktim.model.elastic.forecast.IndexComprehensiveDoc;
import com.tunnelkey.tktim.model.forecast.comprehensive.model.AGFComprehensiveModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
* @Version: 0.0.1
* @Description: 地质综合预报的索引文件接口
* @Author: lxt
* @Date:  2019/2/16 15:12
*/
@Service
public class IndexComprehensiveServiceImpl {
    @Autowired
    @Lazy
    private IndexComprehensiveRepository template;

    @Autowired
    private ESCommon esCommon;

    public void save(AGFComprehensiveModel model){
        IndexComprehensiveDoc doc = new IndexComprehensiveDoc();
        esCommon.setSectionBaseInfo(doc,model.Id,"","","",model.OperatorUserId,model.WorkSectionId);
        doc.setRemark(model.Remark);
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(AGFComprehensiveModel model) {
        template.deleteById(model.Id);
    }

    /**
     * 生成索引的一句简介
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexComprehensiveDoc doc){
        String temp = "综合预报信息。综合结论简述:{0}";
        return MessageFormat.format(
                temp,
                doc.getRemark()
        );
    }
}
