package com.zsy.blog.service;

import com.zsy.blog.entitys.Topic;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.TopicVo;

import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 20:18
 * @desc
 */

public interface TopicService {

    PageInfo<TopicVo> getTopicByPage(int page);

    List<TopicVo> getUserTopic(Integer userId);


}
