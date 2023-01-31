package com.zsy.blog.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.BlogCountDto;
import com.zsy.blog.entitys.*;
import com.zsy.blog.repository.BlogRepository;
import com.zsy.blog.utils.DateUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/21 10:28
 * @desc 管理员操作
 */
@Service
public class SystemManager {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private BlogRepository blogRepository;

    @Resource
    private EntityManager entityManager;

    @Resource
    private ElasticsearchRestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    //初始化ES索引
    public void initEsIndex(){

        IndexOperations blogOperations = restTemplate.indexOps(IndexCoordinates.of(Constants.ES_BLOG_INDEX));

        boolean existsBlogIndex = blogOperations.exists();

        IndexOperations fileOperations = restTemplate.indexOps(IndexCoordinates.of(Constants.ES_FILE_INDEX));

        boolean existsFileIndex =fileOperations .exists();

        if(!existsBlogIndex){
            blogOperations.create();
            blogOperations.putMapping(EsBlog.class);
            initEsBlog();
        }

        if(!existsFileIndex){
            fileOperations.create();
            fileOperations.putMapping(EsFile.class);
        }

    }

    //初始化ES
    public void initEsBlog(){
        List<Blog> blogs = blogRepository.findAll();

        List<EsBlog> esBlogList=blogs.stream().map(blog->{
            boolean topic=blog.getTopic()==null;
            EsBlog esBlog = new EsBlog();
            esBlog.setId(blog.getId());
            esBlog.setTitle(blog.getTitle());
            esBlog.setUid(blog.getUser().getId());
            esBlog.setTopic(!topic);
            esBlog.setDescription(blog.getDescription());
            if(topic) {
                esBlog.setCategory(blog.getCategory().getId());
            }else{
                esBlog.setTopicId(blog.getTopic().getId());
            }
            return esBlog;
        }).collect(Collectors.toList());

        restTemplate.save(esBlogList, IndexCoordinates.of(Constants.ES_BLOG_INDEX));

    }

    //初始化博客
    public void initBlog(){

        redisTemplate.delete(Arrays.asList(Constants.EYE_MAP,Constants.LIKE_MAP,Constants.BLOG_MAP,Constants.HOST_BLOG_SORT,Constants.RANDOM_BLOG));

        List<Blog> blogs = blogRepository.findAll();

        blogs.forEach(blog->{
            try {
                redisTemplate.opsForHash().put(Constants.BLOG_MAP,blog.getId(),objectMapper.writeValueAsString(blog));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            redisTemplate.opsForZSet().add(Constants.HOST_BLOG_SORT,blog.getId(),blog.getEyeCount());
            redisTemplate.opsForSet().add(Constants.RANDOM_BLOG,blog.getId());
        });
    }

    //初始化分类
    public void initCategoryList(){
        redisTemplate.delete(Constants.CATEGORY_LIST);
        String sql="select * from categorys where deleted=0";
        TypedQuery<Category> query = (TypedQuery<Category>) entityManager.createNativeQuery(sql, Category.class);
        query.getResultStream().forEach(category -> {
            redisTemplate.opsForList().leftPush(Constants.CATEGORY_LIST,category.toCategoryVo());
        });
    }

    //初始化标签
    public void initTag(){
        redisTemplate.delete(Constants.RANDOM_BLOG);
        String sql="select * from tags where deleted = 0";
        TypedQuery<Tag> query = (TypedQuery<Tag>) entityManager.createNativeQuery(sql, Tag.class);
        query.getResultStream().forEach(tag->{
            redisTemplate.opsForSet().add(Constants.TAG_CLOUD,tag.toTagVo());
        });
    }

    //将redis浏览量和点赞量更新到数据库
    public void updateEyeCountAndLikCountToDataBase(){

        Set<Integer> like = redisTemplate.opsForHash().keys(Constants.LIKE_MAP);

        //更新点赞数量
        for (Integer key : like) {
            Integer likeCount = (Integer) redisTemplate.opsForHash().get(Constants.LIKE_MAP,key);

            if(likeCount!=null && likeCount>0) {
                blogRepository.updateLikeCount(likeCount,key );
            }

        }

        Set<Integer> eye = redisTemplate.opsForHash().keys(Constants.EYE_MAP);

        //更新浏览数量
        for (Integer key : eye) {
            Integer eyeCount = (Integer) redisTemplate.opsForHash().get(Constants.EYE_MAP,key);

            if(eyeCount!=null && eyeCount>0) {
                blogRepository.updateEyeCount(eyeCount,key);
            }

        }

        redisTemplate.delete(Arrays.asList(Constants.EYE_MAP,Constants.LIKE_MAP));
    }

    @Transactional
    public void initUserLike(){
        String sql = "select user_id,blog_id,like_date from user_likes";
        Query nativeQuery = entityManager.createNativeQuery(sql);
        List<Object[]> list = nativeQuery.getResultList();
        if (list != null && list.size() > 0) {
            list.forEach(result -> {
                Integer userId = (Integer) result[0];
                Integer blogId = (Integer) result[1];
                Date likeDate = (Date) result[2];
                redisTemplate.opsForZSet().add(Constants.USER_LIKES + ":" + userId, blogId, likeDate.getTime());
            });
        }
    }

    @Transactional
    public void initUserStar(){
        String sql = "select user_id,blog_id,star_date from user_stars";
        Query nativeQuery = entityManager.createNativeQuery(sql);
        List<Object[]> list = nativeQuery.getResultList();
        if (list != null && list.size() > 0) {
            list.forEach(result -> {
                Integer userId = (Integer) result[0];
                Integer blogId = (Integer) result[1];
                Date starDate = (Date) result[2];
                redisTemplate.opsForZSet().add(Constants.USER_STARS + ":" + userId, blogId, starDate.getTime());
            });
        }
    }

    @Transactional
    public void updateUserLike(){

        Set<String> keys = redisTemplate.keys(Constants.USER_LIKES + "*");

        if(keys==null||keys.size()==0) {
            return;
        }

        List<String> list=new ArrayList<>();
        for (String key : keys) {
            Integer userId=Integer.valueOf(key.split(":")[1]);
            String sql="delete from user_likes where user_id = ?";
            entityManager.createNativeQuery(sql).setParameter(1,userId).executeUpdate();
            Set<Integer> blogIds = redisTemplate.opsForZSet().range(key, 0, -1);
            blogIds.forEach(id->{
                Double score = redisTemplate.opsForZSet().score(key, id);
                String append = "("+userId+","+id+",'"+ DateUtils.formatDate(new Date(score.longValue())) +"')";
                list.add(append);
            });
            StringBuilder stringBuilder= new StringBuilder("insert into user_likes values");

            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i) + ",");
            }

            stringBuilder.deleteCharAt(stringBuilder.length()-1);

            entityManager.createNativeQuery(stringBuilder.toString()).executeUpdate();
        }
    }

    @Transactional
    public void updateUserStar(){

        Set<String> keys = redisTemplate.keys(Constants.USER_STARS + "*");

        if(keys==null||keys.size()==0) {
            return;
        }

        List<String> list=new ArrayList<>();
        for (String key : keys) {
            Integer userId=Integer.valueOf(key.split(":")[1]);
            String sql="delete from user_stars where user_id = ?";
            entityManager.createNativeQuery(sql).setParameter(1,userId).executeUpdate();
            Set<Integer> blogIds = redisTemplate.opsForZSet().range(key, 0, -1);
            blogIds.forEach(id->{
                Double score = redisTemplate.opsForZSet().score(key, id);
                String append = "("+userId+","+id+",'"+ DateUtils.formatDate(new Date(score.longValue())) +"')";
                list.add(append);
            });
            StringBuilder stringBuilder= new StringBuilder("insert into user_stars values");

            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i) + ",");
            }

            stringBuilder.deleteCharAt(stringBuilder.length()-1);

            entityManager.createNativeQuery(stringBuilder.toString()).executeUpdate();

        }
    }

}
