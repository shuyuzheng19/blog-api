package com.zsy.blog.repository;

import com.zsy.blog.entitys.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 20:17
 * @desc
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic,Integer>, JpaSpecificationExecutor<Topic> {

    @Query(nativeQuery=true,value= "select * from topics where user_id = :id")
    List<Topic> findByUser(Integer id);


    @Query(nativeQuery=true,value= "select count(id) from topics where id = :topicId and user_id = :userId")
    int existsByUser(Integer userId,Integer topicId);


}
