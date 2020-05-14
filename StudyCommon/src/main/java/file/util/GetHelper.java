package file.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.tunnelkey.tktim.ossfile.model.OSSReadObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j(topic = "com.tunnelkey.tktim.ossfile.Application")
public class GetHelper extends OSSBase {
    public InputStream GetStream(String key) {
        OSSClient client = null;
        try {
            client = this.getClient();
            OSSObject ossobject = client.getObject(config.getBucketName(), key);
            return ossobject.getObjectContent();
        } catch (OSSException e) {
            throw e;
        } finally {
            client.shutdown();
        }
    }

    public OSSReadObject GetObject(String key) throws OSSException {
        OSSReadObject object = new OSSReadObject();
        OSSClient client = null;
        try {
            client = this.getClient();
            object.client = client;//用户之后必须关闭
            OSSObject object1 = client.getObject(config.getBucketName(), key);
            object.inputStream = object1.getObjectContent();
            object.isSuccess = true;
        } catch (OSSException e) {
            log.error("从阿里云oss获取文件失败，路径" + key + e.getMessage());
            object.isSuccess = false;
            throw e;
        } finally {
            //client.shutdown();调用方法关闭
        }
        return object;
    }

    public boolean ExistObject(String key) throws OSSException {
        OSSClient client = null;
        try {
            client = this.getClient();
            return client.doesObjectExist(config.getBucketName(), key);
        } catch (OSSException e) {
            throw e;
        } finally {
            client.shutdown();
        }
    }
}
