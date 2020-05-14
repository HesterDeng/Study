package elastic.forecast.repository;

import com.tunnelkey.tktim.model.elastic.forecast.IndexDrillDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
* @Version: 0.0.1
* @Description: 地质钻探的索引数据接口
* @Author: lxt
* @Date:  2019/2/16 14:26
*/
@Repository
public interface IndexDrillRepository extends ElasticsearchRepository<IndexDrillDoc, UUID> {
}
