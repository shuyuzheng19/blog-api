package com.zsy.blog.manager;

import com.zsy.blog.common.Constants;
import com.zsy.blog.repository.BlogRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 郑书宇
 * @create 2023/1/18 22:53
 * @desc
 */
@Service
public class CountManager {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private BlogRepository blogRepository;

    //获取博客浏览量
    public Integer getEyeCount(Integer blogId){
        return (Integer) redisTemplate.opsForHash().get(Constants.EYE_MAP,blogId);
    }

    //获取博客点赞量
    public Integer getLikeCount(Integer blogId){
        return (Integer) redisTemplate.opsForHash().get(Constants.LIKE_MAP,blogId);
    }

    public void incrementEyeCount(Integer blogId){
        redisTemplate.opsForHash().increment(Constants.EYE_MAP,blogId,1);
        redisTemplate.opsForZSet().incrementScore(Constants.HOST_BLOG_SORT,blogId,1);
    }

    public void incrementLikeCount(Integer blogId){
        redisTemplate.opsForHash().increment(Constants.LIKE_MAP,blogId,1);
    }

    //判断当前IP是否已访问过该博客
    public boolean isAccessBlog(String ip,Integer id){
        String md5Ip= DigestUtils.md5DigestAsHex(ip.getBytes());

        boolean isAccess=redisTemplate.opsForHash().hasKey(Constants.IP_BLOG_ACCESS,md5Ip);

        Set<Integer> accessIds=null;

        if(isAccess) {

            accessIds = (Set<Integer>) redisTemplate.opsForHash().get(Constants.IP_BLOG_ACCESS, md5Ip);

            if (accessIds.contains(id)) {
                return true;
            }

        }else{
            accessIds=new HashSet<>();
        }

        accessIds.add(id);

        redisTemplate.opsForHash().put(Constants.IP_BLOG_ACCESS,md5Ip,accessIds);

        return false;

    }

    //博客浏览量+1并返回当前浏览量
    public void addEyeCount(Integer blogId){

        Boolean flag = redisTemplate.opsForHash().hasKey(Constants.EYE_MAP,blogId);

        if(flag) {
            incrementEyeCount(blogId);
        }else{
            Integer eyeCount = blogRepository.findByEyeCount(blogId);
            redisTemplate.opsForHash().put(Constants.EYE_MAP,blogId,eyeCount+1);
        }

    }

    //博客点赞量+1
    public void addLikeCount(Integer blogId){

        Boolean flag = redisTemplate.opsForHash().hasKey(Constants.LIKE_MAP,blogId);

        if(flag) {
            incrementLikeCount(blogId);
        }else{
            Integer likeCount = blogRepository.findByLikeCount(blogId);
            redisTemplate.opsForHash().put(Constants.LIKE_MAP,blogId,likeCount+1);
        }

    }

    //博客点赞量-1
    public void subLikeCount(Integer blogId){
        Boolean flag = redisTemplate.opsForHash().hasKey(Constants.LIKE_MAP,blogId);

        if(flag) {
            redisTemplate.opsForHash().increment(Constants.LIKE_MAP,blogId,-1);
        }else{
            Integer likeCount = blogRepository.findByLikeCount(blogId);
            redisTemplate.opsForHash().put(Constants.LIKE_MAP,blogId,likeCount-1);
        }

    }

}
