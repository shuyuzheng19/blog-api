package com.zsy.blog.controllers.admin;

import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.BlogConditionRequest;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.Topic;
import com.zsy.blog.enums.RoleEnum;
import com.zsy.blog.manager.SystemManager;
import com.zsy.blog.repository.TopicRepository;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.response.Result;
import com.zsy.blog.service.AdminService;
import com.zsy.blog.service.BlogService;
import com.zsy.blog.service.SystemService;
import com.zsy.blog.utils.PageUtils;
import com.zsy.blog.utils.StringUtils;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.MusicVo;
import com.zsy.blog.vos.TopicVo;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/27 12:38
 * @desc
 */
@RestController
@RequestMapping("/api/v1/super")
public class SuperAdminController {

    @Resource
    private TopicRepository topicRepository;

    @Resource
    private BlogService blogService;

    @Resource
    private SystemService systemService;

    @Resource
    private SystemManager systemManager;

    @PostMapping("/add/timeline")
    public Result addTimeLine(@RequestBody String content){
        systemService.addTimeLine(content);
        return Result.ok();
    }


    @DeleteMapping("/delete/timeline")
    public Result deleteTimeLine(Integer id){

        if(id==null || id<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法ID");
        }

        systemService.deleteTimeLine(id);

        return Result.ok();
    }

    @GetMapping("/topic/all")
    public Result getAllTopic(){
        List<Topic> topics = topicRepository.findAll();
        List<TopicVo> topicVoList=topics.stream().map(topic->topic.topicVo()).collect(Collectors.toList());
        return Result.ok(topicVoList);
    }

    @GetMapping("/blog/condition")
    public Result getConditionBlog(@RequestParam(defaultValue = "1")Integer page,@ModelAttribute BlogConditionRequest request){
        Page<Blog> conditionBlog = blogService.getConditionBlog(page, request);
        PageInfo<BlogVo> result = PageUtils.getBlogVoPageInfo(conditionBlog);
        return Result.ok(result);
    }

    @GetMapping("/blog/search")
    public Result getSearchBlog(@RequestParam(defaultValue = "1")Integer page,String keyword){
        PageInfo<BlogVo> pageInfo = blogService.searchBlogs2(keyword,page);
        return Result.ok(pageInfo);
    }

    @GetMapping("/trash/blogs")
    public Result getDeleteBlog(@RequestParam(defaultValue = "1")Integer page,@ModelAttribute BlogConditionRequest request){
        Page<Blog> conditionBlog = blogService.getDeleteBlog(page, request);
        PageInfo<BlogVo> result = PageUtils.getBlogVoPageInfo(conditionBlog);
        return Result.ok(result);
    }

    @PostMapping("/delete/blogs")
    public Result deleteBlogs(@RequestBody List<Integer> ids){
        boolean result=blogService.removeBlog(ids);
        return result?Result.ok():Result.customize(ResultCode.ERROR.value(),"删除失败");
    }

    @PostMapping("/restore/blog")
    public Result restoreDeleteBlog(@RequestBody List<Integer> ids){
        boolean result=blogService.restoreBlog(ids);
        return result?Result.ok():Result.customize(ResultCode.ERROR.value(),"恢复失败");
    }

    @GetMapping("/blog/like")
    public Result likeQueryBlog(@RequestParam(defaultValue = "1") Integer page,String keyword){

        if(StringUtils.isEmpty(keyword)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"有查询的关键字不能为空!");
        }

        Page<Blog> blogs = blogService.likeQueryBlog(page, keyword);
        PageInfo<BlogVo> result = PageUtils.getBlogVoPageInfo(blogs);
        return Result.ok(result);
    }

    @PostMapping("/gonggao")
    public Result setGongGao(@RequestBody String str){
        if(StringUtils.isEmpty(str)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"公告内容不能为空!");
        }
        System.out.println(str);
        systemService.setGongGao(str);
        return Result.ok();
    }

    @GetMapping("/logout/user")
    public Result logoutUser(String username){

        if(StringUtils.isEmpty(username)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"用户名不能为空!");
        }

        systemService.logoutUser(username);

        return Result.ok();
    }

    @GetMapping("/user/role")
    public Result updateUserRole(String username, RoleEnum role){

        if(StringUtils.isEmpty(username)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"用户名不能为空");
        }

        if(role==null){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法角色");
        }

        systemService.updateUserRole(username,role);

        return Result.ok();
    }

    @GetMapping("/delete/category")
    public Result deleteCategory(Integer categoryId){
        if(categoryId==null || categoryId<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法分类ID");
        }
        systemService.deleteCategory(categoryId);
        return Result.ok();
    }

    @GetMapping("/delete/tag")
    public Result deleteTag(Integer tagId){
        if(tagId==null || tagId<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法标签ID");
        }
        systemService.deleteTag(tagId);
        return Result.ok();
    }

    @GetMapping("/delete/topic")
    public Result deleteTopic(Integer topicId){
        if(topicId==null || topicId<=0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"非法专题ID");
        }
        systemService.deleteTopic(topicId);
        return Result.ok();
    }



    @GetMapping("/music/cloud")
    public Result setMusicCloudPlayList(String mid){
        if(StringUtils.isEmpty(mid)){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"缺少网易云歌单ID参数");
        }
        systemService.setMusicToMusicCloud(mid);
        return Result.ok();
    }

    @PostMapping("/music/list")
    public Result setMusicPlayList(@RequestBody List<MusicVo> list){

        if(list==null || list.size()==0){
            return Result.customize(ResultCode.PARAMS_ERROR.value(),"歌单列表为空!");
        }

        systemService.setMusicPlayList(list);

        return Result.ok();

    }

    @GetMapping("/init/blog")
    public Result initBlog(){
        systemManager.initBlog();

        return Result.ok();
    }

    @GetMapping("/init/category")
    public Result initCategory(){
        systemManager.initCategoryList();

        return Result.ok();
    }

    @GetMapping("/init/tag")
    public Result initTag(){
        systemManager.initTag();

        return Result.ok();
    }

    @GetMapping("/init/es")
    public Result initEs(){
        systemManager.initEsBlog();

        return Result.ok();
    }

    @GetMapping("/update/blog")
    public Result updateBlog(){
        systemManager.updateEyeCountAndLikCountToDataBase();
        return Result.ok();
    }

    @GetMapping("/init/like_star")
    public Result initLike(){
        systemManager.initUserLike();
        systemManager.initUserStar();
        return Result.ok();
    }

    @GetMapping("/update/like_star")
    public Result initStar(){
        systemManager.updateUserLike();
        systemManager.updateUserStar();
        return Result.ok();
    }
}
