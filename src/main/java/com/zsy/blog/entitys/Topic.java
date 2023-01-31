package com.zsy.blog.entitys;

import com.zsy.blog.utils.DateUtils;
import com.zsy.blog.vos.TopicVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/17 20:09
 * @desc
 */
@Data
@Entity
@Table(name="topics") @ApiModel(value = "专题表")
@SQLDelete(sql = "update topics set deleted = 1 where id = ?")
@Where(clause = "deleted = 0")
public class Topic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("专题ID")
    private Integer id;

    @Column(name="name",length = 50)
    @ApiModelProperty("专题名称")
    private String name;

    @Column(name="description",length = 100)
    @ApiModelProperty("专题描述")
    private String description;

    @Column(name="cover_image")
    @ApiModelProperty("专题封面")
    private String coverImage;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition = "user_id",referencedColumnName = "id")
    private User user;

    @Column(name="create_time")
    @CreatedDate
    @ApiModelProperty(value = "专题创建时间")
    private Date createTime;

    @Column(name="update_time")
    @LastModifiedDate
    @ApiModelProperty(value = "专题修改时间")
    private Date updateTime;

    @Column(name="deleted",nullable = false)
    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除")
    private Integer deleted=0;

    public static Topic of(Integer id){
        Topic topic=new Topic();
        topic.setId(id);
        return topic;
    }

    public TopicVo topicVo(){
        return TopicVo.builder().
                id(this.id)
                .coverImage(this.coverImage)
                .desc(this.description)
                .name(this.name)
                .dateStr(DateUtils.dateToStr(this.createTime))
                .user(this.user.toUserVo())
                .build();
    }

}
