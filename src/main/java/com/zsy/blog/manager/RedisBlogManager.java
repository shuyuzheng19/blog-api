package com.zsy.blog.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.repository.BlogRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 郑书宇
 * @create 2023/1/19 14:10
 * @desc
 */
@Service
public class RedisBlogManager {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private BlogRepository blogRepository;

    @Resource
    private ObjectMapper objectMapper;


    public void initHotBlog(){


        List<Blog> blogs = redisTemplate.opsForHash().values(Constants.BLOG_MAP);

        for (Blog blog : blogs) {
            redisTemplate.opsForZSet().add(Constants.HOST_BLOG_SORT,blog.getId(),blog.getEyeCount());
        }


    }

    public void initRandom(){

        Integer[] ids = blogRepository.findAllId();

        redisTemplate.opsForSet().add(Constants.RANDOM_BLOG,ids);
    }

    public void initBlog(){

        List<Blog> blogs = blogRepository.findAll();

        blogs.forEach(blog->{
            try {
                redisTemplate.opsForHash().put(Constants.BLOG_MAP,blog.getId(),objectMapper.writeValueAsString(blog));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            redisTemplate.opsForHash().put(Constants.EYE_MAP,blog.getId(),blog.getEyeCount());
            redisTemplate.opsForZSet().add(Constants.HOST_BLOG_SORT,blog.getId(),blog.getEyeCount());
            redisTemplate.opsForSet().add(Constants.RANDOM_BLOG,blog.getId());
        });

    }

}
