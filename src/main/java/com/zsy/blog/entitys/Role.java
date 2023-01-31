package com.zsy.blog.entitys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:15
 * @desc 角色表
 */
@Data @Entity @Table(name="roles") @ApiModel("角色表")
public class Role  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("角色ID")
    private Integer id;

    @ApiModelProperty("角色名称")
    private String name;

    @ApiModelProperty("角色信息")
    private String description;


    public static Role of(int id){
        Role role = new Role();
        role.setId(id);
        return role;
    }

}
