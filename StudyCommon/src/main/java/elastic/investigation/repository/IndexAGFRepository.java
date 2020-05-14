package elastic.investigation.repository;

import com.tunnelkey.tktim.model.elastic.investigation.IndexAGFDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2018/12/18 13:43
 */
@Repository
public interface IndexAGFRepository extends ElasticsearchRepository<IndexAGFDoc, UUID> {
}
