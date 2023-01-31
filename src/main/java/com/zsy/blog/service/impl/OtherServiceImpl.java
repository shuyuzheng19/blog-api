package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.TimeLine;
import com.zsy.blog.service.OtherService;
import com.zsy.blog.vos.AuthorInfoVo;
import com.zsy.blog.vos.MusicVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 23:56
 * @desc
 */
@Service
public class OtherServiceImpl implements OtherService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private EntityManager entityManager;


    @Override
    public AuthorInfoVo getAuthorInfVo() {
        Boolean flag = redisTemplate.hasKey(Constants.AUTHOR_INFO);

        if(flag){
            return (AuthorInfoVo) redisTemplate.opsForValue().get(Constants.AUTHOR_INFO);
        }

        return AuthorInfoVo.of();
    }

    @Override
    public List<MusicVo> getMusicPlayList() {
        Boolean flag = redisTemplate.hasKey(Constants.MUSIC_LIST);

        if(flag){
            return (List<MusicVo>) redisTemplate.opsForValue().get(Constants.MUSIC_LIST);
        }

        return new ArrayList<>();
    }

    @Override
    public String getGongGaoInfo() {
        Object result = redisTemplate.opsForValue().get(Constants.GONGGAO);
        return result==null?"暂无最新公告":result.toString();
    }

    @Override
    public List<TimeLine> getAllTimeLine() {

        String sql="select * from time_line order by date desc ";

        TypedQuery<TimeLine> nativeQuery = (TypedQuery<TimeLine>) entityManager.createNativeQuery(sql, TimeLine.class);

        System.out.println(nativeQuery.getResultList());

        return nativeQuery.getResultList();
    }
}
