package com.zsy.blog.vos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author 郑书宇
 * @create 2023/1/17 23:51
 * @desc
 */
@Data @NoArgsConstructor
public class AuthorInfoVo {

    @NotEmpty(message = "作者名称不能为空")
    private String authorName;
    @NotEmpty(message = "网站名称不能为空")
    private String title;
    @NotEmpty(message = "微信号不能为空")
    private String wxStr;
    @NotEmpty(message = "QQ号不能为空")
    private String qq;
    @NotEmpty(message = "公告内容不能为空")
    private String content;
    @NotEmpty(message = "Github地址不能为空")
    private String github;
    @NotEmpty(message = "邮箱不能为空")
    private String email;
    @NotEmpty(message = "微信二维码图片不能为空")
    private String wxImage;

    public static AuthorInfoVo of(){
        AuthorInfoVo authorInfoVo = new AuthorInfoVo();
        authorInfoVo.setAuthorName("郑书宇");
        authorInfoVo.setTitle("ZSY-BLOG");
        authorInfoVo.setWxStr("xiaoyu2528959216");
        authorInfoVo.setQq("2528959216");
        authorInfoVo.setContent("暂无最新公告");
        authorInfoVo.setGithub("https://www.github.com/shuyuzheng19");
        authorInfoVo.setEmail("shuyuzheng19@gmail.com");
        authorInfoVo.setWxImage("/images/wechat.png");
        return authorInfoVo;
    }

}
