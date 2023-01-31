package com.zsy.blog.service.impl;
import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.Category;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.entitys.Topic;
import com.zsy.blog.repository.CategoryRepository;
import com.zsy.blog.repository.TagRepository;
import com.zsy.blog.repository.TopicRepository;
import com.zsy.blog.service.AdminService;
import com.zsy.blog.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/22 13:18
 * @desc
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final TagRepository tagRepository;

    private final CategoryRepository categoryRepository;

    private final TopicRepository topicRepository;

    private final RedisTemplate redisTemplate;

    @Override
    public List<Tag> getAllTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Topic> getAllTopic() {
        Integer userId= UserUtils.getUserId();
        return topicRepository.findByUser(userId);
    }

    @Override
    public boolean addCategory(String name) {
        Category category=new Category();

        category.setCreateTime(new Date());

        category.setUpdateTime(new Date());

        category.setName(name);

        Category result = categoryRepository.save(category);

        if(result.getId()!=null) {
            redisTemplate.opsForList().leftPush(Constants.CATEGORY_LIST,result.toCategoryVo());
            return true;
        }

        return false;
    }


    @Override
    public boolean addTag(String name) {
        Tag tag=new Tag();
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tag.setName(name);
        Tag result = tagRepository.save(tag);

        if(result.getId()!=null) {
            redisTemplate.opsForSet().add(Constants.TAG_CLOUD,result.toTagVo());
            return true;
        }

        return false;
    }
}
