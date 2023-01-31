package com.zsy.blog.controllers;

import com.zsy.blog.annos.Delay;
import com.zsy.blog.common.Constants;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.BlogSortDto;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.enums.SortEnum;
import com.zsy.blog.manager.CountManager;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.BlogService;
import com.zsy.blog.utils.*;
import com.zsy.blog.vos.ArchiveBlogVo;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.SimpleBlogVo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 10:29
 * @desc 博客相关API
 */
@RestController
@RequestMapping("/api/v1/blogs")
@Api(value = "博客API",tags = {"博客API"})
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    private final CountManager countManager;


    @GetMapping("/list")
    @ApiOperation(value = "获取博客信息列表")
    @Delay(time = 10,count = 30,type = "博客列表")
    public Result getBlogByPage(
            @ApiParam("页数") @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "sort",defaultValue = "EYE")
            @ApiParam("排序方式") SortEnum sort,
            @ApiParam("分类ID")@RequestParam(defaultValue = "-1") Integer sortId

    ){

        Page<Blog> result = blogService.getBlogByPage(new BlogSortDto(page, sort,sortId));

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo=PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);

    }


    @GetMapping("/search")
    @ApiOperation("搜索博客内容")
    @Delay(time=10,count = 15)
    public Result searchBlog(String keyword,@RequestParam(value = "page",defaultValue = "1") Integer page){

        if(StringUtils.isEmpty(keyword)){
            Result.customize(ResultCode.PARAMS_ERROR.value(),"要搜索的关键字不能为空");
        }

        PageInfo<BlogVo> pageInfo = blogService.searchBlogs(keyword, page);

        return Result.ok(pageInfo);
    }


    @GetMapping("/get/{id}")
    @ApiOperation("获取某个博客的详情")
    public Result getBlog(HttpServletRequest request, @PathVariable("id") Integer id){
        Blog blog = blogService.getBlogById(id);

        BlogVo blogVo = blog.toBlogVo();

        if(!countManager.isAccessBlog(request.getRemoteAddr(),id)){
            countManager.addEyeCount(blogVo.getId());
            blogVo.setEyeCount(blog.getEyeCount()+1);
        }

        blogVo.setContent(Base64Utils.decodingToString(blog.getContent(), Charset.forName("UTF-8")));

        return Result.ok(blogVo);
    }

    @GetMapping("/recommend")
    @ApiOperation("获取推荐博客列表")
    public Result getRecommendBlog(){

        List<SimpleBlogVo> recommendBlogs = blogService.getRecommendBlogs();

        return Result.ok(recommendBlogs);
    }

    @GetMapping("/hots")
    @ApiOperation("获取热门博客")
    public Result getHotBlog(){
        List<SimpleBlogVo> blogs = blogService.getHotBlogs();
        return Result.ok(blogs);
    }

    @GetMapping("/random")
    @ApiOperation("随机博客")
    public Result getRandomBlog(){
        List<SimpleBlogVo> blogs = blogService.randomBlog();
        return Result.ok(blogs);
    }

    @GetMapping("/user/{id}")
    @ApiOperation("获取某个用户的博客列表")
    public Result getUserBlogs(@PathVariable("id") Integer id,@RequestParam(value = "page",defaultValue = "1") Integer page){
        if(id==null||id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        Page<Blog> result = blogService.getUserBlogs(id, page,SortEnum.CREATE,true);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo=PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);
    }

    @GetMapping("/user/like/{id}")
    @ApiOperation("获取某个用户的点赞列表")
    public Result getUserLikeBlogs(@PathVariable("id") Integer id,@RequestParam(value = "page",defaultValue = "1") Integer page){

        if(id==null||id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        Page<Blog> result = blogService.getUserLikeBlog(id,page);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo=PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);
    }

    @GetMapping("/user/star")
    @ApiOperation("获取某个用户的点赞列表")
    public Result getUserStarBlogs(@RequestParam(value = "page",defaultValue = "1") Integer page){

        Page<Blog> result = blogService.getUserStarBlog(UserUtils.getUserId(),page);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo=PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);
    }

    private List<BlogVo> getBlogVoList(Page<Blog> page){
        List<BlogVo> blogVos=page.stream().map(blog->{

            BlogVo blogVo = blog.toBlogVo();

            Integer eyeCount = countManager.getEyeCount(blogVo.getId());
            if(eyeCount!=null&&eyeCount>0){
                blogVo.setEyeCount(eyeCount);
            }
            Integer likeCount=countManager.getLikeCount(blogVo.getId());
            if(likeCount!=null&&likeCount>0) {
                blogVo.setLikeCount(countManager.getLikeCount(blog.getId()));
            }

            return blogVo;
        }).collect(Collectors.toList());
        return blogVos;
    }

    @GetMapping("/tag/{id}")
    @ApiOperation("获取某个标签下的博客")
    public Result getTagBlogs(@PathVariable("id") Integer id,@RequestParam(value = "page",defaultValue = "1") Integer page){

        if(id==null||id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        Page<Blog> result = blogService.getTagBlogByPage(id, page);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo=PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);
    }

    @GetMapping("/similar")
    @ApiOperation("获取该博客的相关内容")
    public Result getSimilarBlog(Integer blogId,String keyword){

        if(blogId==null||blogId<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        List<Blog> blogs = blogService.RelevantBlog(keyword, blogId);

        List<SimpleBlogVo> blogVoList=blogs.stream().map(blog->new SimpleBlogVo(blog.getId(),blog.getTitle(),null)).collect(Collectors.toList());

        return Result.ok(blogVoList);
    }

    @GetMapping("/user/{id}/top")
    @ApiOperation("获取用户浏览量排名前10的博客")
    public Result getUserTop10(@PathVariable("id") Integer id){

        if(id==null||id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        List<Blog> result = blogService.getUserBlogTop10(id);

        List<SimpleBlogVo> userBlogTop10 = result.stream().map(blog->new SimpleBlogVo(blog.getId(),blog.getTitle(),null)).collect(Collectors.toList());

        return Result.ok(userBlogTop10);

    }

    @GetMapping("/topic")
    @ApiOperation("获取专题文档列表")
    @Delay(time = 10,count = 20,type = "专题文档列表")
    public Result getTopicDocumentByPage(@RequestParam("id") Integer id,@RequestParam(defaultValue = "1") Integer page){
        if(id==null||id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }
        Page<Blog> result = blogService.getTopicDocumentByPage(id, page);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo=PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);
    }

    @GetMapping("/range")
    @ApiOperation("获取某个时间段的博客")
    @Delay(time = 10,count = 20,type = "博客归档")
    public Result getRangeBlogByPage(@RequestParam("page") Integer page,@ApiParam("开始时间") long start,@ApiParam("结束时间") long end){

        if(start==0||end==0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"请传入开始和结束的时间戳");
        }

        if(start>end){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"开始时间不能大于结束时间");
        }


        Page<Blog> result = blogService.getRangeBlog(DateUtils.formatMill(start), DateUtils.formatMill(end), page);

        List<ArchiveBlogVo> blogVoList = result.getContent().stream().map(blog -> new ArchiveBlogVo(blog.getId(),blog.getTitle(),blog.getDescription(),blog.getCreateTime())).collect(Collectors.toList());

        PageInfo<ArchiveBlogVo> pageInfo = PageUtils.getBlogVoPageInfo(page, 30, result.getTotalElements(), blogVoList);

        return Result.ok(pageInfo);
    }



}
