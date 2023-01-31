package com.zsy.blog.entitys;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zsy.blog.utils.DateUtils;
import com.zsy.blog.vos.BlogVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 7:29
 * @desc 博客表
 */
@Data @Entity @Table(name="blogs") @ApiModel(value = "博客表")
@SQLDelete(sql = "update blogs set deleted = 1 where id = ?")
@Where(clause = "deleted = 0")
@ToString
public class Blog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "博客ID",dataType = "int")
    private Integer id;

    @Column(name = "description",length = 100,nullable = false)
    @ApiModelProperty(value = "博客简介")
    private String description;

    @Column(name = "title",length = 50,nullable = false)
    @ApiModelProperty(value = "博客标题")
    private String title;

    @Column(name = "content",length = 50,nullable = false)
    @ApiModelProperty(value = "博客详情内容")
    private String content;

    @Column(name = "source_url")
    @ApiModelProperty(value = "博客转载链接")
    private String sourceUrl;

    @Column(name="cover_image",nullable = false)
    @ApiModelProperty(value = "博客封面图")
    private String coverImage;

    @ManyToOne(targetEntity = Topic.class,fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition = "topic_id",referencedColumnName = "id")
    private Topic topic;

    @Column(name="eye_count",nullable = false)
    @ApiModelProperty(value = "博客浏览量")
    private Integer eyeCount;

    @Column(name="like_count",nullable = false)
    @ApiModelProperty(value = "博客点赞量")
    private Integer likeCount;

    @ManyToOne(targetEntity = Category.class,fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition = "category_id",referencedColumnName = "id")
    @ApiModelProperty(value = "博客分类信息")
    private Category category;

    @ManyToMany(targetEntity = Tag.class,fetch = FetchType.EAGER)
    @ApiModelProperty(value = "博客标签信息")
    private Set<Tag> tags=new HashSet<>();

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(columnDefinition = "user_id",referencedColumnName = "id")
    @ApiModelProperty(value = "博客用户信息")
    private User user;

    @Column(name="create_time")
    @ApiModelProperty(value = "标签创建时间")
    private Date createTime;

    @Column(name="update_time")
    @ApiModelProperty(value = "标签修改时间")
    private Date updateTime;

    @Column(name="deleted",nullable = false)
    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除")
    private Integer deleted=0;

    public BlogVo toBlogVo(){
        return BlogVo.builder()
                .id(this.id)
                .category(this.category==null?null:this.category.toCategoryVo())
                .tags(
                   this.tags.stream().map(tag->tag.toTagVo()).collect(Collectors.toSet())
                )
                .createDate(this.createTime)
                .updateDate(this.updateTime)
                .dateStr(DateUtils.dateToStr(this.createTime))
                .coverImage(this.coverImage)
                .description(this.description)
                .user(this.user.toUserVo())
                .eyeCount(this.eyeCount)
                .topic(this.topic==null?null:this.topic.topicVo())
                .likeCount(this.likeCount)
                .sourceUrl(this.sourceUrl)
                .title(this.title)
                .build();
    }
}
