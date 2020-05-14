package file.util;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.tunnelkey.tktim.ossfile.config.OSSClientConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class OSSBase {

	@Autowired
	protected OSSClientConfig config;
	public OSSClient getClient() {
		ClientConfiguration conf = new ClientConfiguration();
		conf.setConnectionTimeout(10000);
		return new OSSClient(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret(),
				conf);
	}
}
