package elastic;

import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.elastic.ESearchRequest;
import com.tunnelkey.tktim.model.elastic.ESearchResponse;

public interface ESearchService {

    PageModel<ESearchResponse> search(ESearchRequest request);

}
