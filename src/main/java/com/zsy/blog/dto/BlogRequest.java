package com.zsy.blog.dto;

import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.Category;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.entitys.Topic;
import com.zsy.blog.utils.Base64Utils;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 8:48
 * @desc
 */
@Data @Builder
public class BlogRequest {

    private Integer id;

    @NotEmpty(message = "博客标题不能为空")
    @Size(min = 3,max = 50,message = "博客标题不能小于3个字符并且不能大于50个字符")
    private String title;

    @NotEmpty(message = "博客简介不能为空")
    @Size(min = 3,max = 100,message = "博客标题不能小于3个字符并且不能大于100个字符")
    private String description;

    @NotEmpty(message = "博客内容不能为空")
    private String content;

    private String sourceUrl;

    @NotEmpty(message = "博客封面链接不能为空")
    private String coverImage;

    private Integer eyeCount;

    private Integer likeCount;

    private Integer category;

    private List<Integer> tags;

    private Integer topic;

    public Blog toBlogDo(){
        Blog blog=new Blog();
        blog.setId(this.id);
        blog.setDescription(this.description);
        blog.setTitle(this.title);
        blog.setContent(Base64Utils.encodingToString(this.content));
        blog.setSourceUrl(this.sourceUrl);
        blog.setCoverImage(this.coverImage);
        if(this.topic!=null){
            blog.setTopic(Topic.of(this.topic));
        }
        if(this.category!=null){
            blog.setCategory(Category.of(this.category));
        }
        if(this.tags!=null && this.tags.size()>0){
            blog.setTags(tags.stream().map(id-> Tag.of(id)).collect(Collectors.toSet()));
        }
        blog.setDeleted(0);
        return blog;
    }
}
