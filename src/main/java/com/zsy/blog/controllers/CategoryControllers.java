package com.zsy.blog.controllers;

import com.zsy.blog.response.Result;
import com.zsy.blog.service.CategoryService;
import com.zsy.blog.service.TagService;
import com.zsy.blog.vos.CategoryVo;
import com.zsy.blog.vos.TagVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 17:33
 * @desc
 */
@RestController
@RequestMapping("/api/v1/categorys")
@RequiredArgsConstructor
@Api(value = "CategoryApi",tags = {"分类API"})
public class CategoryControllers {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/get")
    @ApiModelProperty("获取所有分类列表")
    public Result getTagCloud(){
        List<CategoryVo> categoryVoList = categoryService.getCategoryList();
        return Result.ok(categoryVoList);
    }

}
