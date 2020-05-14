package elastic.investigation.repository;

import com.tunnelkey.tktim.model.elastic.investigation.IndexLiningDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （开挖方法索引仓库）
 * @author: enoch
 * @date: 2018/12/18 13:35
 */
@Repository
public interface IndexLiningRepository extends ElasticsearchRepository<IndexLiningDoc, UUID> {
}
