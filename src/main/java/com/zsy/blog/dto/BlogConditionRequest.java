package com.zsy.blog.dto;

import lombok.Data;

/**
 * @author 郑书宇
 * @create 2023/1/27 13:19
 * @desc
 */
@Data
public class BlogConditionRequest {

    private Integer category;

    private Integer topic;

    private String sort;

    private boolean flag;

}
