package com.zsy.blog.service;

import com.zsy.blog.entitys.TimeLine;
import com.zsy.blog.vos.AuthorInfoVo;
import com.zsy.blog.vos.MusicVo;

import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 23:55
 * @desc
 */
public interface OtherService {

    //获取网站用户信息
    AuthorInfoVo getAuthorInfVo();

    //获取网站歌单信息
    List<MusicVo> getMusicPlayList();

    //获取公告信息
    String getGongGaoInfo();

    //获取网站动态信息
    List<TimeLine> getAllTimeLine();

}
