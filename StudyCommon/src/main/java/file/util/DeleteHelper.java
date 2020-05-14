package file.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.springframework.stereotype.Component;

@Component
public class DeleteHelper extends OSSBase {
	public void DeleteObject(String key) {
		OSSClient client = null;
		try {
			client = this.getClient();
			client.deleteObject(config.getBucketName(), key);
		} catch (OSSException oe) {
			oe.printStackTrace();
		} catch (ClientException ce) {
			ce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.shutdown();
		}
	}
}
