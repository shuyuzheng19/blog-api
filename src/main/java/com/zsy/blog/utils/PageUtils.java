package com.zsy.blog.utils;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.vos.BlogVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/19 11:00
 * @desc
 */
public class PageUtils {

    public static PageInfo<BlogVo> getBlogVoPageInfo(Page<Blog> page){
        PageInfo<BlogVo> pageInfo=new PageInfo<>();
        pageInfo.setPage(page.getNumber()+1);
        pageInfo.setSize(page.getSize());
        pageInfo.setTotal(page.getTotalElements());
        pageInfo.setData(page.getContent().stream().map(blog -> blog.toBlogVo()).collect(Collectors.toList()));
        return pageInfo;
    }

    public static <T> PageInfo<T> getBlogVoPageInfo(int page, int size, long total, List<T> data){
        PageInfo<T> pageInfo=new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        pageInfo.setTotal(total);
        pageInfo.setData(data);
        return pageInfo;
    }

}
