package com.zsy.blog.vos;

import lombok.Builder;
import lombok.Data;

/**
 * @author 郑书宇
 * @create 2023/1/21 0:45
 * @desc
 */
@Data @Builder
public class FileInfoVo {

    private String md5;

    private String name;

    private String dateStr;

    private String url;

    private String suffix;

    private long size;
}
