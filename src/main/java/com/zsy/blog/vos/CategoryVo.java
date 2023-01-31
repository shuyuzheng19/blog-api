package com.zsy.blog.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 郑书宇
 * @create 2023/1/17 8:40
 * @desc
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class CategoryVo implements Serializable {

    private Integer id;

    private String name;

}
