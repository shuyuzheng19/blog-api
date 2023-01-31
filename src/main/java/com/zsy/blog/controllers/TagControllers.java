package com.zsy.blog.controllers;

import com.zsy.blog.entitys.Tag;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.TagService;
import com.zsy.blog.vos.TagVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 17:33
 * @desc
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Api(value = "TagApi",tags = {"标签API"})
public class TagControllers {

    @Resource
    private TagService tagService;

    @GetMapping("/cloud")
    @ApiModelProperty("获取标签云")
    public Result getTagCloud(){
        Set<TagVo> tags = tagService.getTagCloud();
        return Result.ok(tags);
    }

}
