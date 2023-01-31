package com.zsy.blog.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.entitys.*;
import com.zsy.blog.enums.RoleEnum;
import com.zsy.blog.repository.BlogRepository;
import com.zsy.blog.repository.UserRepository;
import com.zsy.blog.service.SystemService;
import com.zsy.blog.utils.Base64Utils;
import com.zsy.blog.vos.AuthorInfoVo;
import com.zsy.blog.vos.CategoryVo;
import com.zsy.blog.vos.MusicVo;
import com.zsy.blog.vos.SimpleBlogVo;
import org.elasticsearch.index.query.QueryBuilders;
import org.jsoup.Jsoup;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/18 0:20
 * @desc 超级管理员服务
 */
@Service
public class SystemServiceImpl implements SystemService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ElasticsearchRestTemplate restTemplate;

    @Override
    public void setAuthorInfo(AuthorInfoVo authorInfo) {
        redisTemplate.opsForValue().set(Constants.AUTHOR_INFO,authorInfo);
    }

    @Override
    public void setMusicPlayList(List<MusicVo> list) {
        redisTemplate.opsForValue().set(Constants.MUSIC_LIST,list);
    }

    @Override
    public void setMusicToMusicCloud(String playListId) {
        String url = "https://api.injahow.cn/meting/?type=playlist&id="+playListId;
        try {
            String body = Jsoup.connect(url).ignoreContentType(true).execute().body();
            List<MusicVo> musics = new ObjectMapper().readValue(body, new TypeReference<List<MusicVo>>(){});
            redisTemplate.opsForValue().set(Constants.MUSIC_LIST,musics);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalException(ResultCode.ERROR.value(),"爬取失败,可能接口已失效");
        }

    }

    @Override
    public void setRecommendBlog(List<Integer> ids) {

        redisTemplate.opsForValue().set(Constants.RECOMMEND_KEY,ids);

    }

    @Override
    public void setGongGao(String str) {
        redisTemplate.opsForValue().set(Constants.GONGGAO,str);
    }

    @Override
    public void logoutUser(String username) {
        String md5Username= DigestUtils.md5DigestAsHex(username.getBytes());
        final String KEY=Constants.LOGIN_USER_TOKEN+":"+md5Username;
        boolean exists=redisTemplate.hasKey(KEY);
        if(!exists){
            throw new GlobalException(ResultCode.ERROR.value(),"该账户未登录,不需要注销");
        }
        Boolean delete = redisTemplate.delete(KEY);

        if(!delete){
            throw new GlobalException(ResultCode.ERROR.value(),"注销失败!");
        }
    }

    @Override
    public void updateUserRole(String username, RoleEnum role) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUNT.value(), "找不到该用户"));

        user.setRole(Role.of(role.getId()));

        userRepository.save(user);

        redisTemplate.delete(Constants.USER+":"+user.getUsername());

    }

    @Override
    public void deleteCategory(Integer categoryId) {

        Integer result = userRepository.existsByCategoryId(categoryId);

        if(result==null || result==0){
            throw new GlobalException(ResultCode.NOT_FOUNT.value(),"该分类不存在");
        }

        userRepository.deleteBlogByCategory(categoryId);

        int i =userRepository.deleteCategory(categoryId);

        if(i>0) {

            redisTemplate.delete(Arrays.asList(Constants.BLOG_MAP,Constants.CATEGORY_LIST,Constants.HOST_BLOG_SORT,Constants.RANDOM_BLOG));

            NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();

            nativeSearchQueryBuilder.withQuery((QueryBuilders.termQuery("category",categoryId)));

            restTemplate.delete(nativeSearchQueryBuilder.build(), EsBlog.class, IndexCoordinates.of(Constants.ES_BLOG_INDEX));

        }else{
            throw new GlobalException(ResultCode.ERROR.value(),"删除分类失败!");
        }
    }

    @Override
    public void deleteTag(Integer tagId) {
        Integer result = userRepository.existsByTagId(tagId);

        if(result==null || result==0){
            throw new GlobalException(ResultCode.NOT_FOUNT.value(),"该标签不存在");
        }

        String[] blogIds = userRepository.findTagsBlogId(tagId);

        userRepository.deleteTagBlog(tagId);

        int i1 = userRepository.deleteTag(tagId);

        if(i1>0) {

            NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(QueryBuilders.idsQuery().addIds(blogIds)).build();

            redisTemplate.delete(Arrays.asList(Constants.BLOG_MAP,Constants.TAG_CLOUD,Constants.HOST_BLOG_SORT,Constants.RANDOM_BLOG));

            restTemplate.delete(build, EsBlog.class, IndexCoordinates.of(Constants.ES_BLOG_INDEX));

        }else{
            throw new GlobalException(ResultCode.ERROR.value(),"删除标签失败!");
        }
    }

    @Override
    public void deleteTopic(Integer topicId) {
        Integer result = userRepository.existsByTopicId(topicId);

        if(result==null || result==0){
            throw new GlobalException(ResultCode.NOT_FOUNT.value(),"该专题不存在");
        }


        userRepository.deleteBlogByTopic(topicId);

        int i = userRepository.deleteTopic(topicId);

        if(i>0) {
            redisTemplate.delete(Arrays.asList(Constants.BLOG_MAP,Constants.CATEGORY_LIST,Constants.HOST_BLOG_SORT,Constants.RANDOM_BLOG));

            NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(QueryBuilders.termQuery("topicId",topicId)).build();

            restTemplate.delete(build, EsBlog.class, IndexCoordinates.of(Constants.ES_BLOG_INDEX));

        }else{

            throw new GlobalException(ResultCode.ERROR.value(),"删除专题失败!");
        }
    }

    @Override
    public void addTimeLine(String content) {
        Date date=new Date();

        int result = userRepository.addTimeLine(Base64Utils.encodingToString(content.getBytes()), date);

        if(result==0){
            throw new GlobalException(ResultCode.ERROR.value(),"添加时间线失败!");
        }

    }

    @Override
    public void deleteTimeLine(Integer id) {
        int result = userRepository.deleteTimeLine(id);

        if(result==0){
            throw new GlobalException(ResultCode.ERROR.value(),"删除时间线失败!");
        }
    }

    @Override
    public List<Integer> getRecommendBlog() {

       List<Integer> ids = (List<Integer>) redisTemplate.opsForValue().get(Constants.RECOMMEND_KEY);

       return ids==null? new ArrayList<>():ids;
    }
}
