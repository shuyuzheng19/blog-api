package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.Topic;
import com.zsy.blog.repository.TopicRepository;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.service.TopicService;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.TopicVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 20:19
 * @desc
 */
@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicRepository topicRepository;

    @Override
    public PageInfo<TopicVo> getTopicByPage(int page) {
        Page<Topic> result = topicRepository.findAll(PageRequest.of(page, Constants.TOPIC_PAGE_COUNT, Sort.by(Sort.Order.desc("createTime"))).previousOrFirst());
        PageInfo<TopicVo> pageInfo=new PageInfo<>();
        pageInfo.setPage(result.getNumber());
        pageInfo.setSize(result.getSize());
        pageInfo.setTotal(result.getTotalElements());
        List<TopicVo> topicVoList=result.stream().map(topic->topic.topicVo()).collect(Collectors.toList());
        pageInfo.setData(topicVoList);
        return pageInfo;
    }

    @Override
    public List<TopicVo> getUserTopic(Integer userId) {
        List<TopicVo> result = topicRepository.findByUser(userId).stream().map(topic -> topic.topicVo()).collect(Collectors.toList());
        return result;
    }

}
