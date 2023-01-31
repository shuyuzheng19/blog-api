package com.zsy.blog.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author 郑书宇
 * @create 2023/1/21 10:48
 * @desc
 */
@Data @Builder
public class BlogCountDto {

    private Integer eyeCount;

    private Integer likeCount;

}
