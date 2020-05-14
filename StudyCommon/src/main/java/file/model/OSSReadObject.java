package file.model;

import com.aliyun.oss.OSSClient;

import java.io.InputStream;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2018/11/30 17:00
 */
public class OSSReadObject {

    public InputStream inputStream;

    public OSSClient client;

    public boolean isSuccess;
}
