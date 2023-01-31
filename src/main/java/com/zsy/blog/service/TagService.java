package com.zsy.blog.service;

import com.zsy.blog.entitys.Category;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.vos.TagVo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:05
 * @desc
 */
public interface TagService {
    Optional<Tag> findById(Integer id);

    Set<TagVo> getTagCloud();

    List<Tag> getAllTag();

    void saveTag(Tag tag);
}
