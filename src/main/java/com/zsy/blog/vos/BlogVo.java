package com.zsy.blog.vos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zsy.blog.entitys.Topic;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author 郑书宇
 * @create 2023/1/17 8:44
 * @desc
 */
@Data @Builder @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class BlogVo implements Serializable {

    private Integer id;

    private String description;

    private String title;

    private String content;

    private Date createDate;

    private Date updateDate;

    private String sourceUrl;

    private String coverImage;

    private String dateStr;

    private Integer eyeCount;

    private Integer likeCount;

    private Set<TagVo> tags;

    private TopicVo topic;

    private CategoryVo category;

    private UserVo user;

}
