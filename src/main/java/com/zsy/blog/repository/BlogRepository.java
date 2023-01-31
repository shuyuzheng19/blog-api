package com.zsy.blog.repository;

import com.zsy.blog.dto.BlogRequest;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.vos.SimpleBlogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:04
 * @desc
 */
@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer>, JpaSpecificationExecutor<Blog> {

    @Query(nativeQuery=true,value="select eye_count from blogs where id = :blogId")
    Integer findByEyeCount(Integer blogId);

    @Query(nativeQuery=true,value="select like_count from blogs where id = :blogId")
    Integer findByLikeCount(Integer blogId);

    @Query(nativeQuery=true,value="update blogs set eye_count = :eyeCount,like_count=:likeCount where id = :blogId")
    @Modifying
    @Transactional
    int updateEyeCountAndLikeCountById(Integer blogId,Integer eyeCount,Integer likeCount);

    @Query(nativeQuery=true,value="update blogs set eye_count = :eyeCount where id = :blogId")
    @Modifying
    @Transactional
    int updateEyeCount(Integer eyeCount,Integer blogId);

    @Query(nativeQuery=true,value="update blogs set like_count=:likeCount where id = :blogId")
    @Modifying
    @Transactional
    int updateLikeCount(Integer likeCount,Integer blogId);


    @Query(nativeQuery=true,value="select id from blogs where deleted=0")
    Integer[] findAllId();

    @Query(nativeQuery=true,value="select blog_id from blogs_tags a join tags b where a.tags_id=:id and a.tags_id=b.id order by b.create_time desc")
    Page<Integer> getBlogByTagId(Integer id,Pageable pageable);

    @Query(nativeQuery=true,value="select id from blogs where id=:blogId and user_id = :userId")
    Integer findByIdBlog(Integer blogId,Integer userId);

    @Query(nativeQuery=true,value="select * from blogs where deleted = 1 and category_id=:id")
    Page<Blog> getCategoryDeleteBlogs(Pageable pageable,Integer id);

    @Query(nativeQuery=true,value="select * from blogs where topic_id is not null and deleted = 1 and topic_id=:id")
    Page<Blog> getTopicDeleteBlogs(Pageable pageable,Integer id);

    @Query(nativeQuery=true,value="select * from blogs where deleted = 1")
    Page<Blog> getDeleteBlogs(Pageable pageable);

    @Query(nativeQuery=true,value="update blogs set deleted = 1 where id in :ids")
    @Modifying
    @Transactional
    int logicalDeleteBlog(List<Integer> ids);

    @Query(nativeQuery=true,value="update blogs set deleted = 1 where id in :ids and user_id = :userId")
    @Modifying
    @Transactional
    int logicalDeleteBlog(List<Integer> ids,Integer userId);

    @Query(nativeQuery=true,value="delete from  blogs where id in :ids")
    @Modifying
    @Transactional
    int deleteBlog(List<Integer> ids);

    @Query(nativeQuery=true,value="update blogs set deleted = 0 where id in :ids")
    @Modifying
    @Transactional
    int restoreBlog(List<Integer> ids);

    @Query(nativeQuery=true,value="select * from blogs where deleted=1 and (title like %:keyword% or description like %:keyword%)")
    Page<Blog> likeQueryBlog(Pageable pageable,String keyword);



}
