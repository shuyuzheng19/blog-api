package com.zsy.blog.service;

import com.zsy.blog.enums.RoleEnum;
import com.zsy.blog.vos.AuthorInfoVo;
import com.zsy.blog.vos.MusicVo;

import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/18 0:20
 * @desc
 */
public interface SystemService {

    void setAuthorInfo(AuthorInfoVo authorInfo);

    void setMusicPlayList(List<MusicVo> list);

    void setMusicToMusicCloud(String playListId);

    void setRecommendBlog(List<Integer> ids);

    void setGongGao(String str);

    void logoutUser(String username);

    void updateUserRole(String username, RoleEnum role);

    void deleteCategory(Integer categoryId);

    void deleteTag(Integer tagId);

    void deleteTopic(Integer topicId);

    void addTimeLine(String content);

    void deleteTimeLine(Integer id);

    List<Integer> getRecommendBlog();
}
