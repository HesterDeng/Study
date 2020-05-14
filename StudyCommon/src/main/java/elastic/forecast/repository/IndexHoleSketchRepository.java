package elastic.forecast.repository;

import com.tunnelkey.tktim.model.elastic.forecast.IndexHoleSketchDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
/**
* @Version: 0.0.1
* @Description: 地质洞身素描的索引数据接口
* @Author: lxt
* @Date:  2019/2/16 14:46
*/
@Repository
public interface IndexHoleSketchRepository extends ElasticsearchRepository<IndexHoleSketchDoc, UUID> {
}
