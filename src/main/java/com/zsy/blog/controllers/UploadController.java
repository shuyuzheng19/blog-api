package com.zsy.blog.controllers;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.User;
import com.zsy.blog.manager.UploadManager;
import com.zsy.blog.response.Result;
import com.zsy.blog.utils.FileUtils;
import com.zsy.blog.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 郑书宇
 * @create 2023/1/20 23:04
 * @desc
 */

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@Api(value = "UploadApi",tags = {"上传api"})
public class UploadController {

    @Resource
    private UploadManager uploadManager;

    @PostMapping("/other")
    @ApiOperation("上传文件")
    public Result uploadOtherFile(@RequestParam("file") MultipartFile multipartFile,@RequestParam(value = "public",defaultValue = "false") boolean isPublic){
        User user = UserUtils.getUserDetails().getUser();
        String username=user.getUsername();
        Integer userId=user.getId();
        String url=uploadManager.upload(multipartFile,isPublic,userId,"/"+username+"/others",null, Constants.OTHER_FILE_MAX_SIZE,"文件大小不能超过2GB",null);
        return Result.ok(url);
    }

    @PostMapping("/image")
    @ApiOperation("上传图片文件")
    public Result uploadImageFile(@RequestParam("file") MultipartFile multipartFile){
        User user = UserUtils.getUserDetails().getUser();
        String username=user.getUsername();
        Integer userId=user.getId();
        String url=uploadManager.upload(multipartFile,false,userId,"/"+username+"/images",Constants.IMAGE_TYPES, Constants.IMAGE_FILE_MAX_SIZE,"图片大小不能超过 5MB","这不是一个图片文件 请检查");
        return Result.ok(url);
    }

    @PostMapping("/md")
    @ApiOperation("解析markdown文件")
    public Result uploadMarkDownFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        InputStream inputStream = multipartFile.getInputStream();
        String result= FileUtils.inputStreamToString(inputStream);
        return Result.ok(result);
    }
}
