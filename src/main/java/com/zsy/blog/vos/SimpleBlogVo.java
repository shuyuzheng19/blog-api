package com.zsy.blog.vos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 郑书宇
 * @create 2023/1/17 16:37
 * @desc
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class SimpleBlogVo implements Serializable {
    private Integer id;
    private String title;
    private String coverImage;
}
