package com.zsy.blog.controllers;

import com.zsy.blog.response.Result;
import com.zsy.blog.service.OtherService;
import com.zsy.blog.vos.MusicVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 23:54
 * @desc 其他API
 */
@RestController
@RequestMapping("/api/v1/other")
@Api(value = "其他API",tags = {"其他API"})
@RequiredArgsConstructor
public class OtherController {

    @Resource
    private OtherService otherService;

    @GetMapping("/music")
    @ApiOperation("获取歌单")
    public Result getMusicPlayList(){

        List<MusicVo> musicVoList=otherService.getMusicPlayList();

        return Result.ok(musicVoList);
    }

    @GetMapping("/author")
    @ApiOperation("获取网站信息")
    public Result getAuthorInfo(){
        return Result.ok(otherService.getAuthorInfVo());
    }

    @GetMapping("/gonggao")
    public Result getGongGao(){
        return Result.ok(otherService.getGongGaoInfo());
    }

    @GetMapping("/timeline")
    public Result getTimeLine(){
        return Result.ok(otherService.getAllTimeLine());
    }
}
