package com.zsy.blog.entitys;

import com.zsy.blog.utils.Base64Utils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/31 19:01
 * @desc
 */
@Entity @Table(name="time_line") @Data
public class TimeLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    private Date date;

    public String getContent() {
        return Base64Utils.decodingToString(this.content);
    }
}
