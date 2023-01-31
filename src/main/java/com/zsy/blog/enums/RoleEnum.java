package com.zsy.blog.enums;

/**
 * @author 郑书宇
 * @create 2023/1/28 17:46
 * @desc
 */
public enum RoleEnum {

    USER(1),
    ADMIN(2),
    SUPER_ADMIN(3);

    private Integer id;

    RoleEnum(Integer id){
        this.id=id;
    }

    public Integer getId() {
        return id;
    }
}
