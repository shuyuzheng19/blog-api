package com.zsy.blog.service;

import com.zsy.blog.entitys.Category;
import com.zsy.blog.vos.CategoryVo;
import com.zsy.blog.vos.TagVo;

import java.util.List;
import java.util.Optional;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:05
 * @desc
 */
public interface CategoryService {
    Optional<Category> findById(Integer id);

    List<CategoryVo> getCategoryList();

    void saveCategory(Category category);
}
