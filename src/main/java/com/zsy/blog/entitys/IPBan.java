package com.zsy.blog.entitys;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zsy.blog.enums.Ban;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 郑书宇
 * @create 2023/1/18 13:21
 * @desc 封禁IP
 */
@Data
public class IPBan implements Serializable {

    @ApiModelProperty("md5Ip")
    @Column(name="ip",unique = true)
    @JsonIgnore
    private String id;

    @ApiModelProperty("封禁描述")
    private String description;

    @Column(name="ban",nullable = false)
    @ApiModelProperty(value = "封禁类别 0:频繁访问接口 1:管理员封禁 2:其他原因封禁",name="ban")
    private Ban ban;

    @ApiModelProperty(value = "IP封禁时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime banTime;

    @ApiModelProperty(value = "封禁恢复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime restoreTime;

    @ApiModelProperty(value = "封禁的接口")
    private String url;

    @ApiModelProperty(value = "封禁的接口说明")
    private String type;
}
