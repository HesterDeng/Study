package elastic.forecast.repository;

import com.tunnelkey.tktim.model.elastic.forecast.IndexFaceDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
/**
* @Version: 0.0.1
* @Description: 地质掌子面的索引数据接口
* @Author: lxt
* @Date:  2019/2/16 14:41
*/
@Repository
public interface IndexFaceRepository extends ElasticsearchRepository<IndexFaceDoc, UUID> {
}
