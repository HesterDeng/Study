package elastic.forecast.repository;

import com.tunnelkey.tktim.model.elastic.forecast.IndexHelpMsgDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @Auther: dengguiping
 * @Date: 2019/2/26 16:21
 * @Description:
 */
@Repository
public interface IndexHelpMsgRepository extends ElasticsearchRepository<IndexHelpMsgDoc, UUID> {
}
