package com.zsy.blog.controllers.admin;

import com.zsy.blog.common.Constants;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.BlogRequest;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.Category;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.entitys.Topic;
import com.zsy.blog.enums.SortEnum;
import com.zsy.blog.manager.CountManager;
import com.zsy.blog.repository.TopicRepository;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.AdminService;
import com.zsy.blog.service.BlogService;
import com.zsy.blog.utils.Base64Utils;
import com.zsy.blog.utils.PageUtils;
import com.zsy.blog.utils.StringUtils;
import com.zsy.blog.utils.UserUtils;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/22 13:23
 * @desc
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Resource
    private BlogService blogService;

    @Resource
    private AdminService adminService;

    @Resource
    private TopicRepository topicRepository;

    @Resource
    private CountManager countManager;


    @GetMapping("/current/user/blogs")
    public Result getCurrentUserBlogs(@RequestParam(value = "page",defaultValue = "1") Integer page, SortEnum sort){
        if(page==null || page<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }
        Integer userId= UserUtils.getUserId();

        Page<Blog> result = blogService.getUserBlogs(userId, page, sort,false);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo= PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);

    }

    @GetMapping("/current/topic/blog")
    public Result getCurrentTopicBlog(@RequestParam(value = "page",defaultValue = "1") Integer page,Integer topicId,String sort){

        if(topicId==null || topicId<=0) return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");

        int result = topicRepository.existsByUser(UserUtils.getUserId(),topicId);

        if(result>0) {
            Page<Blog> pageResult = blogService.getTopicBlogs(page, topicId, sort);

            List<BlogVo> blogs=getBlogVoList(pageResult);

            PageInfo<BlogVo> pageInfo =PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,pageResult.getTotalElements(),blogs);

            return Result.ok(pageInfo);
        }else{
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"只能查询自己的专题");
        }

    }

    @PostMapping("/delete/blogs")
    public Result deleteBlogs(@RequestBody List<Integer> ids){
        blogService.deleteBlogByIds(ids);
        return Result.ok();
    }

    @GetMapping("/get/tags")
    public Result getAllTags(){

        List<Tag> tags = adminService.getAllTag();

        if(tags.size()>0) {
            return Result.ok(tags.stream().map(tag->tag.toTagVo()).collect(Collectors.toSet()));
        }else{
            return Result.ok(new ArrayList<>());
        }

    }

    @GetMapping("/get/category")
    public Result getAllCategory(){
        List<Category> categories = adminService.getAllCategory();

        if(categories.size()>0) {
            return Result.ok(categories.stream().map(category->category.toCategoryVo()).collect(Collectors.toSet()));
        }else{
            return Result.ok(new ArrayList<>());
        }
    }

    @GetMapping("/get/topics")
    public Result getAllTopics(){
        List<Topic> topics = adminService.getAllTopic();

        if(topics.size()>0) {
            return Result.ok(topics.stream().map(topic->topic.topicVo()).collect(Collectors.toSet()));
        }else{
            return Result.ok(new ArrayList<>());
        }
    }

    @PostMapping("/add/blog")
    public Result releaseBlog(@RequestBody @Valid BlogRequest blogRequest){

        if(blogRequest.getTopic()==null) {
            if (blogRequest.getCategory() == null || (blogRequest.getTags() == null || blogRequest.getTags().size() == 0)) {
                return Result.customize(ResultCode.PARAMS_ERROR.value(), "请选择博客分类或标签");
            }
        }else{
            blogRequest.setCategory(null);
            blogRequest.setTags(null);
        }

        Blog blog=blogRequest.toBlogDo();

        boolean flag=false;

        if(blog.getId()!=null) {
            flag = blogService.updateBlog(blog);
        }else{
            flag = blogService.releaseBlog(blog);
        }


        return flag?Result.ok():Result.customize(ResultCode.ERROR.value(),"添加失败");
    }

    @GetMapping("/update/blog")
    public Result validatorIsMeBlog(Integer blogId){
        Blog blog = blogService.getBlogById(blogId);
        UserVo user = UserUtils.getUserDetails().getUserVo();
        if(user.isSuperAdmin() || blog.getUser().getId().equals(user.getId())) {
            if(blog.getTopic()==null) {
                List<Integer> tags = blog.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toList());
                BlogRequest result = BlogRequest.builder().category(blog.getCategory().getId())
                        .tags(tags).content(Base64Utils.decodingToString(blog.getContent()))
                        .coverImage(blog.getCoverImage()).sourceUrl(blog.getSourceUrl()).description(blog.getDescription())
                        .title(blog.getTitle()).id(blog.getId()).build();
                return Result.ok(result);
            }else{
                BlogRequest result = BlogRequest.builder().content(Base64Utils.decodingToString(blog.getContent()))
                        .coverImage(blog.getCoverImage()).description(blog.getDescription()).topic(blog.getTopic().getId())
                        .title(blog.getTitle()).id(blog.getId()).sourceUrl(blog.getSourceUrl()).build();
                return Result.ok(result);
            }
        }else{
            return Result.customize(ResultCode.NOT_FOUNT.value(),"这不是您发布的博客 不允许修改!");
        }
    }

    @PostMapping("/save/preview_markdown")
    public Result savePreViewContent(@RequestBody String content){
        blogService.savePreviewBlog(content);
        return Result.ok();
    }

    @GetMapping("/get/preview")
    public Result getPreViewContent(){
        String content=blogService.getPreviewBlog();
        return Result.ok(content);
    }

    @PostMapping("/save/user_markdown")
    public Result saveUserMarkdownContent(@RequestBody String content){
        blogService.saveUserMarkdown(content);
        return Result.ok();
    }

    @GetMapping("/get/user_markdown")
    public Result getUserMarkdown(){
        String content=blogService.getSaveUserMarkdown();
        return Result.ok(content);
    }

    @GetMapping("/current/user/search")
    public Result getCurrentSearchBlogs(@RequestParam(value = "page",defaultValue = "1") Integer page,String keyword){

        if(page==null || page<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        Integer userId= UserUtils.getUserId();

        Page<Blog> result = blogService.searchUserBlog(userId,page,keyword,false);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo= PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);

    }

    @PostMapping("/add/category")
    public Result addCategory(String name){
        if(StringUtils.isEmpty(name)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"分类名称不能为空!");
        }

        boolean flag = adminService.addCategory(name);

        return flag?Result.ok():Result.customize(ResultCode.ERROR.value(),"添加失败 可能该分类已存在!");
    }

    @PostMapping("/add/tag")
    public Result addTag(String name){
        if(StringUtils.isEmpty(name)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"分类名称不能为空!");
        }

        boolean flag = adminService.addTag(name);

        return flag?Result.ok():Result.customize(ResultCode.ERROR.value(),"添加失败 可能该标签已存在!");
    }


    @GetMapping("/current/user/topic/search")
    public Result getCurrentSearchTopicBlogs(@RequestParam(value = "page",defaultValue = "1") Integer page,String keyword){

        if(page==null || page<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法参数");
        }

        Integer userId= UserUtils.getUserId();

        Page<Blog> result = blogService.searchUserBlog(userId,page,keyword,true);

        List<BlogVo> blogVos=getBlogVoList(result);

        PageInfo<BlogVo> pageInfo= PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE,result.getTotalElements(),blogVos);

        return Result.ok(pageInfo);

    }

    @SuppressWarnings("all")
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

}
