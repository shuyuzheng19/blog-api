package com.zsy.blog.controllers;

import com.zsy.blog.annos.Delay;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.TopicService;
import com.zsy.blog.vos.TopicVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 20:34
 * @desc
 */
@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Api(value = "TopicsApi",tags = {"专题api"})
public class TopicController {

    @Resource
    private TopicService topicService;

    @GetMapping("/list/{page}")
    @ApiOperation("获取专题列表")
    @Delay(time = 10,count = 15,type = "专题列表")
    public Result getTopicsByPage(@PathVariable("page") Integer page){

        PageInfo<TopicVo> pageInfo = topicService.getTopicByPage(page);

        return Result.ok(pageInfo);
    }

    @GetMapping("/user/{id}")
    @ApiOperation("获取用户专题")
    @Delay(time = 10,count = 20,type = "用户专题列表")
    public Result getUserTopic(@PathVariable("id") Integer id){

        if(id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        List<TopicVo> userTopic = topicService.getUserTopic(id);

        return Result.ok(userTopic);
    }

}
