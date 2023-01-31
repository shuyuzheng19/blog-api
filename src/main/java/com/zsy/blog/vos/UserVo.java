package com.zsy.blog.vos;

import lombok.Builder;
import lombok.Data;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:26
 * @desc
 */
@Data @Builder
public class UserVo {
    private Integer id;

    private String username;

    private String nickName;

    private boolean admin;

    private boolean superAdmin;

    private String roleName;

    private String icon;
}
