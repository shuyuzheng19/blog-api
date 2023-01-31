package com.zsy.blog.entitys;

import com.zsy.blog.enums.SexEnum;
import com.zsy.blog.enums.UserStatusEnum;
import com.zsy.blog.vos.UserVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/16 15:47
 * @desc
 */
@Data @ApiModel("用户表") @Entity @Table(name="users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "用户ID",name="id",dataType = "int")
    private Integer id;

    @Column(name="username",length = 16,nullable = false,unique = true)
    @ApiModelProperty(value = "用户账号",name="username",dataType = "String")
    private String username;

    @Column(name="password",nullable = false)
    @ApiModelProperty(value = "用户密码",name="password",dataType = "String")
    private String password;

    @Column(name="nick_name",length = 20,nullable = false)
    @ApiModelProperty(value = "用户名称",name="nickName",dataType = "String")
    private String nickName;

    @Column(name="email",nullable = false)
    @ApiModelProperty(value = "用户邮箱",name="email",dataType = "String")
    private String email;

    @Column(name="icon",nullable = false)
    @ApiModelProperty(value = "用户头像",name="icon",dataType = "String")
    private String icon;

    @ApiModelProperty(value = "创建时间",name="create_time",dataType = "String")
    @CreatedDate
    private Date createTime;

    @ApiModelProperty(value = "修改时间",name="update_time",dataType = "String")
    @LastModifiedDate
    private Date updateTime;

    @Column(name="sex",nullable = false)
    @ApiModelProperty(value = "用户性别 0:男 1:女",name="sex",dataType = "int")
    @Enumerated
    private SexEnum sex=SexEnum.MAN;

    @ManyToOne(targetEntity = Role.class)
    @JoinColumn(columnDefinition = "role_id",referencedColumnName = "id")
    private Role role=Role.of(1);

    @Column(name="status",nullable = false)
    @ApiModelProperty(value = "用户状态 0:正常 1:异常",name="status",dataType = "int")
    @Enumerated
    private UserStatusEnum status=UserStatusEnum.NORMA;


    //转换成VO对象
    public UserVo toUserVo(){
        if(this==null || this.status==UserStatusEnum.ABNORMAL){
            return null;
        }
        String roleName = this.role.getName();
        return UserVo.builder()
                .id(this.id)
                .username(this.username)
                .icon(this.icon)
                .nickName(this.nickName)
                .roleName(roleName)
                .admin(roleName.equals("ADMIN") || roleName.equals("SUPER_ADMIN"))
                .superAdmin(roleName.equals("SUPER_ADMIN"))
                .build();

    }
}
