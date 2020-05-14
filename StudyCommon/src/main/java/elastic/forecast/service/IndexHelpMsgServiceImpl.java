package elastic.forecast.service;

import com.tunnelkey.tktim.business.elastic.forecast.repository.IndexHelpMsgRepository;
import com.tunnelkey.tktim.model.elastic.forecast.IndexHelpMsgDoc;
import com.tunnelkey.tktim.model.system.HelpMsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Auther: dengguiping
 * @Date: 2019/2/26 16:30
 * @Description:
 */
@Service
public class IndexHelpMsgServiceImpl {

    @Autowired

@Lazy
    private IndexHelpMsgRepository template;

    public void save(HelpMsgModel model) {
        IndexHelpMsgDoc doc = new IndexHelpMsgDoc();
        doc.setKeyId(model.Id);
        doc.setAuthor(model.Author);
        doc.setSource(model.Source);
        doc.setTitle(model.Title);
        doc.setSummary(model.Summary);
        doc.setCreateTime(LocalDateTime.now());
        doc.setContent(this.toContent(doc));
        template.save(doc);
    }

    public void delete(UUID modelId) {
        template.deleteById(modelId);
    }

    /**
     * 生成索引的一句简介
     *
     * @param doc 索引数据
     * @return 索引值
     */
    private String toContent(IndexHelpMsgDoc doc) {
        String temp = "标题：{0},作者：{1}，来源:{2}，简要:{3}";
        return MessageFormat.format(
                temp,
                doc.getTitle(),
                doc.getAuthor(),
                doc.getSource(),
                doc.getSummary()
        );
    }
}
