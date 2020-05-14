package elastic.forecast.repository;

import com.tunnelkey.tktim.model.elastic.forecast.IndexPilotDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
/**
* @Version: 0.0.1
* @Description: 地质导坑的索引数据数据接口
* @Author: lxt
* @Date:  2019/2/16 14:47
*/
@Repository
public interface IndexPilotRepository extends ElasticsearchRepository<IndexPilotDoc, UUID> {
}
