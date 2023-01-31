package com.zsy.blog.service;

import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.Category;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.entitys.Topic;

import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/22 12:59
 * @desc 管理员服务
 */
public interface AdminService {
    List<Tag> getAllTag();

    List<Category> getAllCategory();

    List<Topic> getAllTopic();

    boolean addCategory(String name);

    boolean addTag(String name);


}
