package file.repository;

import com.aliyun.oss.model.ObjectMetadata;
import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.base.file.*;
import com.tunnelkey.tktim.ossfile.model.OSSReadObject;
import com.tunnelkey.tktim.ossfile.util.GetHelper;
import com.tunnelkey.tktim.ossfile.util.PutHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j(topic = "com.tunnelkey.tktim.ossfile.Application")
public class OSSStoreImpl implements IOSSRepository {

    @Value("${multipartConfig.temp.filePath}")
    private String tempFileDir;

    ExecutorService taskExecutor = Executors.newCachedThreadPool();
    @Autowired
    private PutHelper putHelper;
    @Autowired
    private GetHelper getHelper;
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 初始化中配置的文件类型数据
     */
    @Autowired
    private FileInfoTypes fileInfoTypes;

    @Override
    public OSSFileResponse Save(FileInfoRequest fileRequest) {
        OSSFileResponse response = new OSSFileResponse();
        UUID fileId = UUID.randomUUID();
        response.setFileId(fileId);
        if (fileRequest == null) {
            response.setMsg("参数为空");
            return response;
        }
        if (StringUtils.isEmpty(fileRequest.getFileName())) {
            response.setMsg("文件名称不能为空");
            return response;
        }
        String ext = getExt(fileRequest.getFileName());// fileRequest.getFileName().substring(fileRequest.getFileName().lastIndexOf(".") + 1);
        String filename = String.format("%s.%s", fileId.toString(), ext);
        String key = getKey(filename);
        response.setKey(key);
        response.setFileName(fileRequest.getFileName());
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(fileRequest.getContentType());
        meta.addUserMetadata("Content-Type", fileRequest.getContentType());
        try {
            boolean isresult = putHelper.PutStream(key, fileRequest.inputStream, meta);
            if (!isresult) {
                response.setSuccess(false);
            } else {
                insertFileStore(transToFileInfo(response, fileRequest));
                response.setSuccess(true);
            }
        } catch (Exception e) {
            log.error("保存文件到阿里云的oss异常" + e.getMessage());
            //将文件存到本地【单机部署时用】
//            insertFileStore(transToFileInfo(response, fileRequest));
//            this.saveFileToLocal(fileRequest.inputStream, response.getKey());
//            response.setSuccess(true);
        }
        return response;
    }

    @Override
    public OSSFileResponse Save(FileInfoRequest fileRequest, CountDownLatch latch) {
        OSSFileResponse save = null;
        try {
            save = this.Save(fileRequest);
        } catch (Exception e) {
            log.error("保存文件到阿里云的oss异常" + e.getMessage());
            throw e;
        } finally {
            latch.countDown();
        }
        return save;
    }

    @Override
    public OSSMuitlFileResponse MultiSave(List<FileInfoRequest> files) {
        Lock lock = new ReentrantLock();
        CountDownLatch latch = new CountDownLatch(files.size());
        OSSMuitlFileResponse ret = new OSSMuitlFileResponse();
        files.forEach(file -> {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    OSSFileResponse save = Save(file);
                    lock.lock();
                    try {
                        ret.files.add(save);
                        latch.countDown();
                    } finally {
                        lock.unlock();
                    }
                }
            });
        });
        try {
            latch.await();
            if (files.size() == 1) {
                ret.isSignle = true;
                OSSFileResponse ossResponse = ret.files.get(0);
                ret.setFileId(ossResponse.getFileId());
                ret.setFileName(ossResponse.getFileName());
                ret.setKey(ossResponse.getKey());
            }
        } catch (InterruptedException e) {
            ret.setSuccess(false);
            ret.setMsg(e.getMessage());
        }
        return ret;
    }

    @Override
    public FileContentResponse Read(OSSRequest request) {
        FileContentResponse item = new FileContentResponse();
        item.setFileId(request.getFileId());
        FileInfoModel fileInfo = null;
        try {
            fileInfo = this.GetFileInfo(item.getFileId());
        } catch (Exception e) {
            log.error("从文件库获取文件失败" + item.getFileId() + e.getMessage());
        }
        if (!ObjectUtils.isEmpty(fileInfo)) {
            try {
                OSSReadObject ossReadObject = getHelper.GetObject(fileInfo.Key);
                if (ossReadObject.isSuccess) {
                    item.setContent(input2byte(ossReadObject.inputStream));
                    item.setContentType(fileInfo.ContentType);
                    item.setFileName(fileInfo.FileName);
                    if (ossReadObject.client != null)
                        ossReadObject.client.shutdown();
                }
            } catch (Exception e) {
                //从本地获取文件【单机部署时用】

//                String partPath = fileInfo.Path;
//                InputStream localFile = this.getLocalFile(partPath);
//                item.setContent(input2byte(localFile));
//                item.setContentType(fileInfo.ContentType);
//                item.setFileName(fileInfo.FileName);
            }

        }
        return item;
    }

    @Override
    public boolean Exists(UUID fileId) {
        FileInfoModel fileinfo = this.GetFileInfo(fileId);
        if (fileinfo != null) {
            return getHelper.ExistObject(fileinfo.Key);
        } else {
            return false;
        }
    }

    @Override
    public FileInfoModel GetFileInfo(UUID fileid) {
        Query query = new Query(Criteria.where("_id").is(fileid));
        return mongoTemplate.findOne(query, FileInfoModel.class);
    }

    @Override
    public List<FileInfoModel> GetFileInfos(List<UUID> fileidList) {
        Query query = new Query(Criteria.where("_id").in(fileidList));
        return mongoTemplate.find(query, FileInfoModel.class);
    }

    public byte[] input2byte(InputStream inputStream) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            System.out.println("文件大小" + inputStream.available());
            int num = inputStream.read(buffer);
            while (num != -1) {
                baos.write(buffer, 0, num);
                num = inputStream.read(buffer);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getExt(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String getKey(String filename) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String datestr = LocalDateTime.now().format(formatter);
        String key = String.format("%s/%s", datestr, filename);
        return key;
    }

    private FileInfoModel transToFileInfo(OSSFileResponse response, FileInfoRequest file) {
        FileInfoModel info = new FileInfoModel();
        info.FileId = response.getFileId();
        info.CreateTime = LocalDateTime.now();
        info.FileName = file.getFileName();
        info.ContentType = file.getContentType();
        info.Ext = getExt(file.getFileName());
        info.Path = info.FullPath = response.getKey();
        info.ToStore = true;
        info.Key = response.getKey();
        info.OperatorCompanyId = file.getCompanyId();
        info.OperatorUserId = file.getUserId();
        try {
            info.FileType = file.getFileType();//文件业务类型
        } catch (Exception e) {
            info.FileType = "0";
        }
        return info;
    }

    public void insertFileStore(FileInfoModel fileInfo) {
        mongoTemplate.save(fileInfo);
    }

    @Override
    public PageModel<FileUploadedResponse> getUploadedFileList(FileUploadedRequest request) {
        Query query = new Query();
        if (request.OperatorCompanyId != null && !request.OperatorCompanyId.equals(new UUID(0, 0))) {
            query.addCriteria(Criteria.where("OperatorCompanyId").is(request.OperatorCompanyId));
        }

        if (request.fileTypes != null && !request.fileTypes.isEmpty()) {
            List<String> types = this.fileTypeDeal(request.fileTypes);
            if (types != null) {
                List<Criteria> list = new ArrayList<>();
                types.forEach(type -> list.add(Criteria.where("Ext").regex(type, "i")));
                query.addCriteria(new Criteria().orOperator(list.toArray(new Criteria[0])));
            }
        }
        if (request.fileTypeList != null && !request.fileTypeList.isEmpty()) {
            query.addCriteria(Criteria.where("FileType").in(request.fileTypeList));
        }
        if (!StringUtils.isEmpty(request.keyWord)) {
            query.addCriteria(Criteria.where("FileName").regex(request.keyWord));
        }

        if(!ObjectUtils.isEmpty(request.startTime)&&ObjectUtils.isEmpty(request.endTime)){
            query.addCriteria(Criteria.where("CreateTime").gte(request.startTime));
        }else if(!ObjectUtils.isEmpty(request.endTime)&&ObjectUtils.isEmpty(request.startTime)){
            query.addCriteria(Criteria.where("CreateTime").lte(request.endTime));
        }else if(!ObjectUtils.isEmpty(request.endTime)&&!ObjectUtils.isEmpty(request.startTime)){
            query.addCriteria(Criteria.where("CreateTime").gte(request.startTime).lte(request.endTime));
        }

        PageModel<FileUploadedResponse> pageModel = new PageModel<>();
        if (request.Page != null) {
            long pageNum = request.Page.PageNum;
            long pageSize = request.Page.PageSize;
            pageModel.PageSize = pageSize;
            pageModel.PageNum = pageNum;
            long recordCount = mongoTemplate.count(query, FileInfoModel.class);
            pageModel.setRecordCount(recordCount);
            if (pageNum > 0 && pageSize > 0) {
                query.skip((pageNum - 1) * pageSize).limit((int) pageSize);
            }
        }
        query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
        List<FileInfoModel> fileInfoModels = mongoTemplate.find(query, FileInfoModel.class);
        List<FileUploadedResponse> result = new ArrayList<>();
        fileInfoModels.forEach(item -> {
            FileUploadedResponse response = new FileUploadedResponse();
            response.fileId = item.FileId;
            response.fileName = item.FileName;
            response.ext = item.Ext;
            response.companyId = item.OperatorCompanyId;
            response.createTime = item.CreateTime;
            response.fileType = this.fileTypeTrans(item.Ext);
            result.add(response);
        });
        pageModel.DataList = result;
        return pageModel;
    }

    @Override
    public FileContentResponse getZipFile(OSSZipRequest request) throws IOException {
        FileContentResponse response = new FileContentResponse();
        List<UUID> list = request.getFileIds().stream().map(UUID::fromString).collect(Collectors.toList());
        if (list.isEmpty()) {
            return response;
        }
        List<FileInfoModel> fileInfoModels = this.GetFileInfos(list);
        File tempFile = new File(tempFileDir + "/" + UUID.randomUUID().toString());
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tempFile));
        fileInfoModels.forEach(file -> {
            try {
                OSSReadObject ossReadObject = getHelper.GetObject(file.Key);
                if (ossReadObject.isSuccess) {
                    ZipEntry entry = new ZipEntry(file.FileName);
                    try {
                        zipOutputStream.putNextEntry(entry);
                        zipOutputStream.write(input2byte(ossReadObject.inputStream));
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (ossReadObject.client != null) ossReadObject.client.shutdown();

                }
            } catch (Exception e) {
                //从本地获取文件【单机部署时用】
//                String path = file.Path;
//                InputStream localFile = this.getLocalFile(path);
//                ZipEntry entry = new ZipEntry(file.FileName);
//                try {
//                    zipOutputStream.putNextEntry(entry);
//                    zipOutputStream.write(input2byte(localFile));
//                    zipOutputStream.closeEntry();
//                } catch (IOException ioe) {
//                    e.printStackTrace();
//                }

            }

        });
        zipOutputStream.close();
        FileInputStream fileInputStream = new FileInputStream(tempFile);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        Files.delete(tempFile.toPath());
        response.setContent(bytes);
        return response;
    }


    /**
     * 文件查询类型
     *
     * @param fileTypes 处理后的文件类型
     * @return
     */
    private List<String> fileTypeDeal(List<String> fileTypes) {
        List<String> result = new ArrayList<>();
        if (fileTypes != null) {
            fileTypes.forEach(item -> {
                List<String> list = fileInfoTypes.fileTypeMapping.get(item);
                if (list != null) {
                    result.addAll(list);
                }
            });
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * 转换后缀名 到类型
     *
     * @param ext 后缀名
     * @return 转换后类型
     */
    private String fileTypeTrans(String ext) {
        //临时map，存放遍历获取到的文件类型进行存放
        final Map<String, String> tempMap = new HashMap<>();
        fileInfoTypes.fileTypeMapping.forEach((k, v) -> {
            if (v.stream().anyMatch(item -> item.equals(ext))) {
                tempMap.put(ext, k);
            }
        });
        return StringUtils.isEmpty(tempMap.get(ext)) ? "file" : tempMap.get(ext);//是否存在类型,默认返回类型为 file 文件类型
    }

    /**
     * 保存文件到本地
     *
     * @param inputStream
     * @param partPath
     */
    private void saveFileToLocal(InputStream inputStream, String partPath) {
        OutputStream os = null;
        try {
            String path = "D:\\testFile\\";
            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件
            File tempFile = new File(path);

            if (!tempFile.exists()) {

                tempFile.mkdirs();
            }

            os = new FileOutputStream(tempFile.getPath() + File.separator + partPath);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取本地文件
     *
     * @param partPath
     * @return
     */
    private InputStream getLocalFile(String partPath) {
        File f = new File("D:\\testFile\\" + partPath);
        try {
            InputStream out = new FileInputStream(f);
            return out;
        } catch (Exception e) {
            return null;
        }
    }
}
