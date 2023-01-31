package com.zsy.blog.vos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 郑书宇
 * @create 2023/1/18 0:03
 * @desc
 */
@Data @NoArgsConstructor
public class MusicVo implements Serializable {

    private String name;
    
    private String artist;
    
    private String url;

    private String pic;

    private String lrc;
    
    
}
