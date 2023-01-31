package com.zsy.blog.entitys;

import com.zsy.blog.vos.TagVo;
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
 * @create 2023/1/17 7:47
 * @desc 标签表
 */
@Data
@Entity
@Table(name="tags")
public class Tag  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name",nullable = false,unique = true)
    @ApiModelProperty("标签名称")
    private String name;


    @Column(name="create_time")
    @CreatedDate
    @ApiModelProperty(value = "标签创建时间")
    private Date createTime;

    @Column(name="update_time")
    @LastModifiedDate
    @ApiModelProperty(value = "标签修改时间")
    private Date updateTime;

    public static Tag of(Integer id){
        Tag tag=new Tag();
        tag.setId(id);
        return tag;
    }

    public TagVo toTagVo(){
        return new TagVo(this.id,this.name);
    }
}
