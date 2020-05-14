package file.controller;

import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.base.file.*;
import com.tunnelkey.tktim.ossfile.repository.IOSSRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2018/11/27 14:39
 */
@RestController
@Api(value = "FileServer",description = "文件服务")
public class FileServer {
    @Autowired
    private IOSSRepository iossRepository;
    @ApiOperation(value = "上传文件",notes = "单个文件上传",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "上传报文",required = true,dataType = "MultipartFile")
    })
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
        @ResponseBody
        public OSSFileResponse uploadFile(@RequestBody MultipartFile file) throws IOException {
            byte[] bytes = file.getBytes();
            InputStream is = new ByteArrayInputStream(bytes, 0, bytes.length);
            FileInfoRequest request = new FileInfoRequest();
            request.setAsync(false);
            request.setFileName(file.getOriginalFilename());
        request.setContentType(file.getContentType());
        request.setInputStream(is);
        OSSFileResponse save = iossRepository.Save(request);
        return save;
    }
    /**
     * 多文件上传
     *
     * @param files
     * @param uid
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "上传文件",notes = "支持<=3个文件同时批量上传",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "上传文件内容",required = true,dataType = "MultipartFile"),
            @ApiImplicitParam(name = "uid",value = "用户id",required = true,dataType = "UUID"),
            @ApiImplicitParam(name = "cid",value = "企业id",required = true,dataType = "UUID"),
            @ApiImplicitParam(name = "fileType",value = "文件类型",required = true,dataType = "String"),
            @ApiImplicitParam(name = "contentTypes",value = "文件mime类型，|分割",required = true,dataType = "String")
    })
    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "multipart/form-data")
    public OSSMuitlFileResponse save(@RequestPart(value = "files") MultipartFile[] files, @RequestParam(value = "uid") UUID uid, @RequestParam(value = "cid") UUID cid, @RequestParam(value = "fileType") String fileType,@RequestParam(value = "contentTypes") String contentTypes) throws IOException {
        //return new OSSMuitlFileResponse();
        List<FileInfoRequest> requests = new ArrayList<>();
        String[] split = contentTypes.split("\\|");
        int index=0;
        for(MultipartFile file : files){
            byte[] bytes = new byte[0];
            try {
                bytes = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream is = new ByteArrayInputStream(bytes, 0, bytes.length);
            FileInfoRequest request = new FileInfoRequest();
            request.setUserId(uid);
            request.setCompanyId(cid);
            request.setAsync(false);
            request.setFileName(file.getOriginalFilename());
            request.setContentType(split[index]);//文件类型不能少，不让不知道文件类型
            request.setFileType(fileType);
            request.setInputStream(is);
            requests.add(request);
            index++;
        }
        OSSMuitlFileResponse save = iossRepository.MultiSave(requests);
        return save;
    }
    @ApiOperation(value = "读取文件",notes = "因为url读取收费，所以这里读取文件流内容免费",httpMethod = "POST")
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public FileContentResponse read(@RequestBody OSSRequest request) {
        return iossRepository.Read(request);
    }

    @ApiOperation(value = "获取文件信息",notes = "获取文件信息",httpMethod = "POST")
    @RequestMapping(value = "/getFileInfo", method = RequestMethod.POST)
    public FileInfoModel getFileInfo(@RequestBody UUID fileId){
        return iossRepository.GetFileInfo(fileId);
    }

    @ApiOperation(value = "获取文件信息",notes = "获取文件信息",httpMethod = "POST")
    @RequestMapping(value = "/getFileInfos", method = RequestMethod.POST)
    public List<FileInfoModel> getFileInfos(@RequestBody List<UUID> fileIdList){
        return iossRepository.GetFileInfos(fileIdList);
    }

    @ApiOperation(value = "保存",notes = "保存图片等信息",httpMethod = "POST")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public OSSFileResponse save(@RequestBody FileInfoRequest request) {
        return iossRepository.Save(request);
    }

    @ApiOperation(value = "获取文件列表",notes = "获取已经上传的文件列表",httpMethod = "POST")
    @RequestMapping(value = "/getUploadedFileList", method = RequestMethod.POST)
    public PageModel<FileUploadedResponse> getUploadedFileList(@RequestBody FileUploadedRequest request) {
        return iossRepository.getUploadedFileList(request);
    }

    @ApiOperation(value = "获取文件列表",notes = "获取已经上传的文件列表",httpMethod = "POST")
    @RequestMapping(value = "/downloadZip", method = RequestMethod.POST)
    public FileContentResponse downloadZip(@RequestBody OSSZipRequest request) throws IOException {
        return iossRepository.getZipFile(request);
    }
}
