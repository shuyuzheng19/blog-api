package com.zsy.blog.service;

/**
 * @author 郑书宇
 * @create 2023/1/19 2:07
 * @desc
 */
public interface AuthService {

    void likeBlog(Integer userId,Integer blogId);

    void unlikeBlog(Integer userId,Integer blogId);

    boolean isLike(Integer userId,Integer blogId);

    void starBlog(Integer userId,Integer blogId);

    void unStarBlog(Integer userId,Integer blogId);

    boolean isStar(Integer userId,Integer blogId);

}
