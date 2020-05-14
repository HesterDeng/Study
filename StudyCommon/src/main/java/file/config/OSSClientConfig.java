package file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration()
@ConfigurationProperties(prefix = "ossconfig")
public class OSSClientConfig {
	/**
	 * 文件服务器地址
	 */
	private String endpoint;
	/**
	 * 访问阿里云的oss存储的keyid
	 */
	private String accessKeyId;
	/**
	 * 访问阿里云的oss存储的密钥
	 */
	private String accessKeySecret;
	/**
	 * 访问阿里云的oss存储的的水桶名称（可以理解为存储单元）
	 */
	private String bucketName;
}
