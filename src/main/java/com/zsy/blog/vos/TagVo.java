package com.zsy.blog.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author 郑书宇
 * @create 2023/1/17 8:40
 * @desc
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class TagVo implements Serializable {

    private Integer id;

    private String name;
}