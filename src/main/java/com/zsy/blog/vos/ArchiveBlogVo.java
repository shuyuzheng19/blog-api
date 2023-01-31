package com.zsy.blog.vos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zsy.blog.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/17 23:19
 * @desc
 */
@Data @AllArgsConstructor
public class ArchiveBlogVo {

    private Integer id;

    private String title;

    private String desc;

    private Date createDate;

}
