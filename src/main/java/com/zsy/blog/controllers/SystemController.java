package com.zsy.blog.controllers;

import com.zsy.blog.common.ResultCode;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.SystemService;
import com.zsy.blog.utils.StringUtils;
import com.zsy.blog.vos.MusicVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 书宇
 * @create 2023/1/18 0:20
 * @desc 超级管理员接口
 */
@RestController
@RequestMapping("/api/v1/system")
@Api(value = "超级管理员API",tags = {"超级管理员API"})
@RequiredArgsConstructor
public class SystemController {

    @Resource
    private SystemService systemService;

    @GetMapping("/music/cloud")
    @ApiOperation("添加网易云歌单")
    public Result setMusicPlayList(String mid){
        if(StringUtils.isEmpty(mid)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"缺少参数 mid(网易云歌单ID)");
        }
        systemService.setMusicToMusicCloud(mid);

        return Result.ok();
    }

    @PostMapping("/music")
    @ApiOperation("自定义歌单ID")
    public Result setPlayList(@RequestBody List<MusicVo> musicVoList){
        if(musicVoList.size()==0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"歌单不能为空");
        }
        systemService.setMusicPlayList(musicVoList);

        return Result.ok();
    }

    @GetMapping("/recommend/get")
    @ApiOperation("添加推荐博客列表")
    public Result getRecommendBlog(){

        List<Integer> result = systemService.getRecommendBlog();

        return Result.ok(result);
    }

    @PostMapping("/recommend/add")
    @ApiOperation("添加推荐博客列表")
    public Result setRecommendBlog(@RequestBody List<Integer> ids){

        if(ids==null||ids.size()<4){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"需要添加4个博客ID");
        }

        systemService.setRecommendBlog(ids);

        return Result.ok();
    }


}
