package elastic.forecast.repository;

import com.tunnelkey.tktim.model.elastic.forecast.IndexComprehensiveDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
* @Version: 0.0.1
* @Description: 地质综合预报的索引文件数据接口
* @Author: lxt
* @Date:  2019/2/16 14:39
*/
@Repository
public interface IndexComprehensiveRepository extends ElasticsearchRepository<IndexComprehensiveDoc, UUID> {
}
