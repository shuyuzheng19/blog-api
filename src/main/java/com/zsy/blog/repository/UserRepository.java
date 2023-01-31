package com.zsy.blog.repository;

import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.User;
import com.zsy.blog.vos.CategoryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:12
 * @desc
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);


    /******************与USER表无关************************/
    //删除分类
    @Query(nativeQuery=true,value="delete from categorys where id = :categoryId")
    @Modifying
    @Transactional
    int deleteCategory(Integer categoryId);

    @Query(nativeQuery=true,value="delete from tags where id = :tagId")
    @Modifying
    @Transactional
    int deleteTag(Integer tagId);

    @Query(nativeQuery=true,value="delete from blogs_tags where tags_id = :tagId")
    @Modifying
    @Transactional
    int deleteTagBlog(Integer tagId);

    @Query(nativeQuery=true,value="delete from topics where id = :topicId")
    @Modifying
    @Transactional
    int deleteTopic(Integer topicId);

    @Query(nativeQuery=true,value="select blog_id from blogs_tags where tags_id = :tagId")
    String[] findTagsBlogId(Integer tagId);

    @Query(nativeQuery=true,value="delete from blogs where category_id = :categoryId")
    @Modifying
    @Transactional
    int deleteBlogByCategory(Integer categoryId);

    @Query(nativeQuery=true,value="delete from blogs where topic_id is not null and topic_id = :topicId")
    @Modifying
    @Transactional
    int deleteBlogByTopic(Integer topicId);


    @Query(nativeQuery=true,value="select count(id) from categorys where id = :categoryId")
    Integer existsByCategoryId(Integer categoryId);

    @Query(nativeQuery=true,value="select count(id) from tags where id = :tagId")
    Integer existsByTagId(Integer tagId);

    @Query(nativeQuery=true,value="select count(id) from topics where id = :topicId")
    Integer existsByTopicId(Integer topicId);

    @Query(nativeQuery=true,value="select * from blogs where deleted=0 and category_id= :categoryId")
    List<Blog> findByCategoryId(Integer categoryId);

    @Query(nativeQuery=true,value="insert into time_line(content,date) values(:content,:date)")
    @Modifying
    @Transactional
    int addTimeLine(String content, Date date);

    @Query(nativeQuery=true,value="delete from time_line where id = :id")
    @Modifying
    @Transactional
    int deleteTimeLine(Integer id);

    //转我提下
    @Query(nativeQuery=true,value="select id from blogs where topic_id is not null and topic_id = :topicId")
    String[] findTopicBlog(Integer topicId);

}
