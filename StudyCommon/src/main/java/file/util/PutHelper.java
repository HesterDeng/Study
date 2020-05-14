package file.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;

@Component
public class PutHelper extends OSSBase {

	public PutHelper() {
		System.out.println("--------------Construct" + "Construct");
	}

	public boolean PutStream(String key, java.io.InputStream stream) {
		boolean ok = false;
		OSSClient client = null;
		try {
			client=this.getClient();
			client.putObject(config.getBucketName(), key, stream);
			ok = true;
		} catch (OSSException e) {
			throw e;
		} catch (ClientException e) {
			throw e;
		} finally {
			if(client!=null) {
				client.shutdown();
			}
		}
		return ok;
	}

	public boolean PutStream(String key, InputStream stream, ObjectMetadata meta) {
		boolean ok = false;
		OSSClient client = null;
		try {
			client=this.getClient();

			PutObjectResult result = client.putObject(config.getBucketName(), key, stream, meta);

			if (!StringUtils.isEmpty(result.getRequestId())) {
				ok = true;
			}
		} catch (OSSException ex) {
			throw ex;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(client!=null) {
				client.shutdown();
			}
		}
		return ok;
	}

//	public static void AsyncPutStream(String key, InputStream stream, AsyncCallback callback, UUID fileId) {
//		try {
//			PutResult rt = new PutResult();
//			rt.client = client;
//			rt.FileId = fileId;
//			rt.Key = key;
//			client.BeginPutObject(OSSConfig.BucketName, key, stream, callback, rt);
//		} catch (OssException ex) {
//			throw ex;
//		} catch (Exception ex) {
//			throw ex;
//		}
//	}
//
//	public static void AsyncPutStream(string key, Stream stream, ObjectMetadata meta, AsyncCallback callback,
//			Guid fileId) {
//		try {
//			PutResult rt = new PutResult();
//			rt.client = client;
//			rt.FileId = fileId;
//			rt.Key = key;
//			client.BeginPutObject(OSSConfig.BucketName, key, stream, meta, callback, rt);
//		} catch (OssException ex) {
//			throw ex;
//		} catch (Exception ex) {
//			throw ex;
//		}
//	}

	public void PutFile(String key, String path) {
		OSSClient client = null;
		try {
			client=this.getClient();
			File file = new File(path);
			client.putObject(config.getBucketName(), key, file);
		} catch (OSSException ex) {
			throw ex;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(client!=null) {
				client.shutdown();
			}
		}
	}
//	public static void AsyncPutFile(string key, string path, AsyncCallback callback, Guid fileId) {
//		try {
//			PutResult rt = new PutResult();
//			rt.client = client;
//			rt.FileId = fileId;
//			client.BeginPutObject(OSSConfig.BucketName, key, path, callback, rt);
//		} catch (OssException ex) {
//			throw ex;
//		} catch (Exception ex) {
//			throw ex;
//		}
//	}

//	public static void ModifyMetaData(String key, ObjectMetadata meta) {
//		try {
//
//			client.ModifyObjectMeta(config.getBucketName(), key, meta);
//		} catch (OssException ex) {
//			throw ex;
//		} catch (Exception ex) {
//			throw ex;
//		}
//	}
}
