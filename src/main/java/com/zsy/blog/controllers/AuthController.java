package com.zsy.blog.controllers;

import com.zsy.blog.annos.Delay;
import com.zsy.blog.manager.CountManager;
import com.zsy.blog.response.Result;
import com.zsy.blog.manager.MyUserDetails;
import com.zsy.blog.service.AuthService;
import com.zsy.blog.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 郑书宇
 * @create 2023/1/17 0:50
 * @desc
 */
@RestController
@RequestMapping("/api/v1/auth")
@Api(value = "需要登陆之后才能操作的API",tags = {"登陆后操作的Api"})
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private CountManager countManager;

    @GetMapping("/user")
    @ApiOperation(value = "获取当前登录的用户",tags = "登陆后操作的Api")
    public Result getCurrentUser(){

        MyUserDetails userDetails = UserUtils.getUserDetails();

        if(userDetails==null){
            return Result.fail();
        }

        return Result.ok(userDetails.getUserVo());
    }

    @GetMapping("/like/on")
    @ApiOperation("博客点赞")
    @Delay(time = 10,count = 12,type = "点赞太过频繁")
    public Result likeBlog(@RequestParam("id") Integer blogId){
        Integer userId=UserUtils.getUserId();
        authService.likeBlog(userId,blogId);
        countManager.addLikeCount(blogId);
        return Result.ok(countManager.getLikeCount(blogId));
    }

    @GetMapping("/like/un")
    @ApiOperation("博客取消点赞")
    @Delay(time = 10,count = 12,type = "点赞太过频繁")
    public Result unlikeBlog(@RequestParam("id") Integer blogId){
        Integer userId=UserUtils.getUserId();
        authService.unlikeBlog(userId,blogId);
        countManager.subLikeCount(blogId);
        return Result.ok(countManager.getLikeCount(blogId));
    }

    @GetMapping("/like/is/{id}")
    public Result isLikeBlog(@PathVariable("id") Integer blogId){
        Integer userId=UserUtils.getUserId();
        boolean like = authService.isLike(userId, blogId);
        return Result.ok(like);
    }

    @GetMapping("/star/on")
    @ApiOperation("博客收藏")
    @Delay(time = 10,count = 12,type = "收藏太过频繁")
    public Result starBlog(@RequestParam("id") Integer blogId){
        Integer userId=UserUtils.getUserId();
        authService.starBlog(userId,blogId);
        return Result.ok(countManager.getLikeCount(blogId));
    }

    @GetMapping("/star/un")
    @ApiOperation("博客取消收藏")
    @Delay(time = 10,count = 12,type = "收藏太过频繁")
    public Result unstarBlog(@RequestParam("id") Integer blogId){
        Integer userId=UserUtils.getUserId();
        authService.unStarBlog(userId,blogId);
        return Result.ok(countManager.getLikeCount(blogId));
    }

    @GetMapping("/star/is/{id}")
    public Result isStarBlog(@PathVariable("id") Integer blogId){
        Integer userId=UserUtils.getUserId();
        boolean like = authService.isStar(userId, blogId);
        return Result.ok(like);
    }

}
