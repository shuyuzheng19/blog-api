package com.zsy.blog.controllers;

import com.zsy.blog.annos.Delay;
import com.zsy.blog.dto.FilterFileDto;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.FileService;
import com.zsy.blog.vos.FileInfoVo;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 郑书宇
 * @create 2023/1/21 1:16
 * @desc
 */
@RestController
@RequestMapping("/api/v1/file")
@Api(value = "FileAPI",tags = {"文件API"})
public class FileController {

    @Resource
    private FileService fileService;


    @PostMapping("/get")
    @Delay(time=10,count=20)
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result getCurrentUserFile(@RequestBody FilterFileDto fileDto){
        PageInfo<FileInfoVo> currentUserFileOrFileList = fileService.getCurrentUserFileOrFileList(fileDto, true);
        return Result.ok(currentUserFileOrFileList);
    }

    @PostMapping("/list")
    @Delay(time=10,count=20)
    public Result getFileList(@RequestBody FilterFileDto fileDto){
        PageInfo<FileInfoVo> currentUserFileOrFileList = fileService.getCurrentUserFileOrFileList(fileDto, false);
        return Result.ok(currentUserFileOrFileList);
    }

}
