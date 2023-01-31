package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.service.AuthService;
import io.lettuce.core.ScriptOutputType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 郑书宇
 * @create 2023/1/19 2:07
 * @desc
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private RedisTemplate redisTemplate;

    /*
        Set<Integer> likes = (Set<Integer>) redisTemplate.opsForHash().get(Constants.USER_LIKES, userId);

        if(likes==null) {
            likes = new HashSet<>();
        }

        if(likes.contains(blogId)){
            throw new GlobalException(ResultCode.MATCH_ERROR.value(),"点赞失败,不需要重复点赞");
        }

        likes.add(blogId);

        redisTemplate.opsForHash().put(Constants.USER_LIKES,userId,likes);
     */

    @Override
    public void likeBlog(Integer userId, Integer blogId) {

        final String key=Constants.USER_LIKES+":"+userId;

        Long isLike=redisTemplate.opsForZSet().rank(key,blogId);

        if(isLike==null) {
            redisTemplate.opsForZSet().add(key, blogId, new Date().getTime());
        }else{
            throw new GlobalException(ResultCode.MATCH_ERROR.value(),"点赞失败,不需要重复点赞");
        }

    }

    /*
        Set<Integer> likes = (Set<Integer>) redisTemplate.opsForHash().get(Constants.USER_LIKES, userId);

        if(likes!=null && likes.contains(blogId)) {
            likes.remove(blogId);
            redisTemplate.opsForHash().put(Constants.USER_LIKES, userId, likes);
        }else{
            throw new GlobalException(ResultCode.MATCH_ERROR.value(),"取消点赞失败,检测到你没有点赞");
        }
     */

    @Override
    public void unlikeBlog(Integer userId, Integer blogId) {
        final String key=Constants.USER_LIKES+":"+userId;

        Long isLike=redisTemplate.opsForZSet().rank(key,blogId);

        if(isLike!=null) {
            redisTemplate.opsForZSet().remove(key,blogId);
        }else{
            throw new GlobalException(ResultCode.MATCH_ERROR.value(),"取消点赞失败,你还未点赞,不能取消");
        }
    }

    /*
        Set<Integer> likes = (Set<Integer>) redisTemplate.opsForHash().get(Constants.USER_LIKES, userId);

        if(likes==null){
            return false;
        }

        return likes.contains(blogId);
     */

    @Override
    public boolean isLike(Integer userId, Integer blogId) {
        final String key=Constants.USER_LIKES+":"+userId;

        Long isLike=redisTemplate.opsForZSet().rank(key,blogId);

        return isLike==null?false:true;
    }

    @Override
    public void starBlog(Integer userId, Integer blogId) {
        final String key=Constants.USER_STARS+":"+userId;

        Long isStar=redisTemplate.opsForZSet().rank(key,blogId);

        if(isStar==null) {
            redisTemplate.opsForZSet().add(key, blogId, new Date().getTime());
        }else{
            throw new GlobalException(ResultCode.MATCH_ERROR.value(),"收藏失败,不需要重复收藏");
        }
    }

    @Override
    public void unStarBlog(Integer userId, Integer blogId) {
        final String key=Constants.USER_STARS+":"+userId;

        Long isStar=redisTemplate.opsForZSet().rank(key,blogId);

        if(isStar!=null) {
            redisTemplate.opsForZSet().remove(key,blogId);
        }else{
            throw new GlobalException(ResultCode.MATCH_ERROR.value(),"取消收藏失败,你还未收藏,不能取消");
        }
    }

    @Override
    public boolean isStar(Integer userId, Integer blogId) {
        final String key=Constants.USER_STARS+":"+userId;

        Long isStar=redisTemplate.opsForZSet().rank(key,blogId);

        return isStar==null?false:true;
    }
}
