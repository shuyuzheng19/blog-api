package com.zsy.blog.dto;

import com.zsy.blog.enums.SortEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:17
 * @desc
 */
@Data @AllArgsConstructor
public class BlogSortDto {

    private Integer page;

    private SortEnum sort;

    private Integer sortId;

}
