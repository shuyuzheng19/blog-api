package com.zsy.blog.manager;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author 郑书宇
 * @create 2023/1/19 0:39
 * @desc
 */
@Service
public class TimingUpdateManager {

    @Resource
    private SystemManager systemManager;

    //定时更新热门博客内容
//    @Scheduled(fixedRate = 1000*60*60*30)
//    public void updateRanking(){
//        try{
//            systemManager.initHotBlog();
//        }catch (GlobalException e){
//            systemManager.initBlogs();
//        }
//    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void redisEyeCountToMySQL(){
        systemManager.updateEyeCountAndLikCountToDataBase();
    }

    //定时更新用户点赞和收藏
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateUserLike(){
        systemManager.updateUserLike();
        systemManager.updateUserStar();
    }

    @PostConstruct
    public void run(){
        systemManager.initEsIndex();
        systemManager.initBlog();
    }
}
