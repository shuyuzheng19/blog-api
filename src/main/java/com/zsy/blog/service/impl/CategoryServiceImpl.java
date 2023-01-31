package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.Category;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.repository.CategoryRepository;
import com.zsy.blog.repository.TagRepository;
import com.zsy.blog.service.BlogService;
import com.zsy.blog.service.CategoryService;
import com.zsy.blog.vos.CategoryVo;
import com.zsy.blog.vos.TagVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:06
 * @desc
 */
@Service
public class CategoryServiceImpl implements CategoryService {


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CategoryRepository categoryRepository;

    @Override
    public Optional<Category> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<CategoryVo> getCategoryList() {

        Boolean flag = redisTemplate.hasKey(Constants.CATEGORY_LIST);

        if(!flag) {

            List<Category> categories = categoryRepository.findAll();

            List<CategoryVo> categoryVoList=categories.stream().map(category -> category.toCategoryVo()).collect(Collectors.toList());

            redisTemplate.opsForList().leftPushAll(Constants.CATEGORY_LIST,categoryVoList);

            return redisTemplate.opsForList().range(Constants.CATEGORY_LIST,0,-1);
        }

        List<CategoryVo> tags = redisTemplate.opsForList().range(Constants.CATEGORY_LIST,0,-1);

        return tags;
    }

    @Override
    public void saveCategory(Category category) {

    }
}
