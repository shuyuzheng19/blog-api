package com.zsy.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author 郑书宇
 * @create 2023/1/21 0:57
 * @desc
 */
@Data
public class FilterFileDto {

    private String keyword;

    private boolean flag;

    private Integer page=1;

    private Integer size=10;

    private Integer sortType=0;
}
