package com.zsy.blog.vos;

import lombok.Builder;
import lombok.Data;

/**
 * @author 郑书宇
 * @create 2023/1/17 20:14
 * @desc
 */
@Data @Builder
public class TopicVo {

    private Integer id;

    private String name;

    private String desc;

    private String dateStr;

    private String coverImage;

    private UserVo user;

}
