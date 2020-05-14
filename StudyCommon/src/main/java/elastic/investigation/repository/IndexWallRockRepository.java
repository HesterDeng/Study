package elastic.investigation.repository;

import com.tunnelkey.tktim.model.elastic.investigation.IndexWallRockDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2018/12/18 13:35
 */
@Repository
public interface IndexWallRockRepository extends ElasticsearchRepository<IndexWallRockDoc, UUID> {
}
