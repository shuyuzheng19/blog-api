package com.zsy.blog.enums;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:15
 * @desc
 */
public enum SortEnum {

    EYE("eyeCount"),//按浏览量排序
    LIKE("likeCount"),//按点赞量排序
    UPDATE("updateTime"),//按修改时间排序
    CREATE("createTime");//按创建时间排序

    private String type;

    SortEnum(String type){
        this.type=type;
    }

    public String colName(){
        return type;
    }

}
