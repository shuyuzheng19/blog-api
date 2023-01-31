package com.zsy.blog.service;

import com.zsy.blog.dto.BlogConditionRequest;
import com.zsy.blog.dto.BlogSortDto;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.enums.SortEnum;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.SimpleBlogVo;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:06
 * @desc
 */
public interface BlogService {

    //获取博客列表
    Page<Blog> getBlogByPage(BlogSortDto sortRequest);

    //获取专题文章
    Page<Blog> getTopicDocumentByPage(int documentId, int page);

    //获取标签文章
    Page<Blog> getTagBlogByPage(int tagId,int page);

    //获取推荐的博客
    List<SimpleBlogVo> getRecommendBlogs();

    //获取某个用户的博客
    Page<Blog> getUserBlogs(int userId, int page, SortEnum sortEnum,boolean isTopic);

    //搜索某个用户的博客
    Page<Blog> searchUserBlog(int userId,int page,String keyword,boolean topic);

    //获取某个用户的点赞博客
    Page<Blog> getUserLikeBlog(int userId,int page);

    //获取某个用户的收藏博客
    Page<Blog> getUserStarBlog(int userId,int page);

    //获取用户博客Top10
    List<Blog> getUserBlogTop10(int userId);

    //获取博客详情
    Blog getBlogById(Integer id);

    //批量获取博客
    List<Blog> batchGetBlog(List<Integer> ids);

    //获取热门博客
    List<SimpleBlogVo> getHotBlogs();

    //随机获取博客
    List<SimpleBlogVo> randomBlog();

    //搜索博客并且高亮
    PageInfo<BlogVo> searchBlogs(String keyword,int page);

    //搜索博客不搞亮
    PageInfo<BlogVo> searchBlogs2(String keyword,int page);

    //相关文章
    List<Blog> RelevantBlog(String keyword,Integer blogId);

    //将数据库数据导入到ES
    void blogToEs();

    //将数据库数据导入到Redis
    void blogToRedis();

    //获取某个时间段之间的数据
    Page<Blog> getRangeBlog(String startTime, String endTime, int page);

    //临时保存预览文章
    void savePreviewBlog(String content);

    //获取保存的预览文章
    String getPreviewBlog();

    //用户保存的信息内容
    void saveUserMarkdown(String content);

    //获取用户保存博客的内容
    String getSaveUserMarkdown();

    //发布博客文章
    boolean releaseBlog(Blog blog);

    //修改博客文章
    boolean updateBlog(Blog blog);

    //逻辑删除博客
    void deleteBlogByIds(List<Integer> ids);

    //获取某个专题下的博客
    Page<Blog> getTopicBlogs(Integer page,Integer topicId,String sort);

    //条件查询博客
    Page<Blog> getConditionBlog(Integer page, BlogConditionRequest request);

    //条件查询已删除的博客
    Page<Blog> getDeleteBlog(Integer page,BlogConditionRequest request);

    //恢复已删除的博客
    boolean restoreBlog(List<Integer> ids);

    //彻底删除博客
    boolean removeBlog(List<Integer> ids);

    //模糊查询博客
    Page<Blog> likeQueryBlog(Integer page,String keyword);
}
