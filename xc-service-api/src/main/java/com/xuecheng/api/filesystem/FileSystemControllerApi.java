package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件接口",description = "对文件的相关操作")
public interface FileSystemControllerApi {
    @ApiOperation("文件上传接口")
    //文件上传
    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey,String metadata);
}

