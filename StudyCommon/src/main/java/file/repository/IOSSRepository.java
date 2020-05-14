package file.repository;

import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.base.file.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @version:（1.0.0.0）
 * @Description:OSS存储到阿里云平台
 * @author: enoch
 * @date: 2018年11月12日 下午5:52:48
 */
public interface IOSSRepository {
	/**
	 *
	* @Description:（保存文件）
	* @param fileRequest
	* @return（展示方法参数和返回值）
	* @date: 2018年11月12日.下午6:21:08
	* @throws
	 */
	OSSFileResponse Save(FileInfoRequest fileRequest);

	OSSFileResponse Save(FileInfoRequest fileRequest, CountDownLatch latch);
	/**
	 * 多文件上传
	 * @param files
	 * @return
	 */
	OSSMuitlFileResponse MultiSave(List<FileInfoRequest> files);
	/**
	* @Description:（读取文件）
	* @param request
	* @return（展示方法参数和返回值）
	* @date: 2018年11月12日.下午6:21:29
	* @throws
	 */
	FileContentResponse Read(OSSRequest request);
	/**
	* @Description:（文件是否存在）
	* @return（展示方法参数和返回值）
	* @date: 2018年11月12日.下午6:21:45
	* @throws
	 */
	boolean Exists(UUID fileId);
	/**
	 *
	* @Description:（文件信息获取）
	* @param fileid
	* @return（展示方法参数和返回值）
	* @date: 2018年11月12日.下午6:21:57
	* @throws
	 */
	FileInfoModel GetFileInfo(UUID fileid);

	List<FileInfoModel> GetFileInfos(List<UUID> fileidList);

	void insertFileStore(FileInfoModel fileInfo);

	/**
	 * 获取已经上传的文件列表
	 * @param request 查询请求
	 * @return 结果集
	 */
	PageModel<FileUploadedResponse> getUploadedFileList(FileUploadedRequest request);

	/**
	 * 获取zip文件
	 * @param request 请求
	 * @return 结果
	 */
	FileContentResponse getZipFile(OSSZipRequest request) throws IOException;
}
