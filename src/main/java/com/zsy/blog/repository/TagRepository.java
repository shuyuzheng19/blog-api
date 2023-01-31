package com.zsy.blog.repository;

import com.zsy.blog.entitys.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:04
 * @desc
 */
@Repository
public interface TagRepository extends JpaRepository<Tag,Integer> {
    
}
