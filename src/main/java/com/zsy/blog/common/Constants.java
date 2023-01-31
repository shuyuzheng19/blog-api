package com.zsy.blog.common;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/16 19:45
 * @desc
 */
public interface Constants {

    /*文件大小*/
    long KB=1024;
    long MB=1024*1024;
    long GB=1024*1024*1024;
    long TB=1024*1024*1024*1024;

    /*文件类型*/
    List<String> IMAGE_TYPES= Arrays.asList("png","jpg","gif","ico","jpeg");
    List<String> VIDEO_TYPES= Arrays.asList("mp4");
    List<String> AUDIO_TYPES= Arrays.asList("mp3");

    //管理员
    String[] ADMIN_ARRAY={"ADMIN","SUPER_ADMIN"};

    /*其他文件最大大小*/
    long OTHER_FILE_MAX_SIZE=2*GB;
    /*图片文件最大大小*/
    long IMAGE_FILE_MAX_SIZE=5*MB;

    /*上海时区*/
    ZoneId DEFAULT_ZONE=ZoneId.of("Asia/Shanghai");

    /*一页有多少条博客数据*/
    int PAGE_SIZE=10;

    /*一页有多少条专题数据*/
    int TOPIC_PAGE_COUNT=30;

    /*归档一页显示多少条数据*/
    int ARCHIVE_PAGE_COUNT=30;

    /*Redis Key*/
    //推荐博客
    String RECOMMEND_KEY="RECOMMEND-BLOG";

    //标签云
    String TAG_CLOUD="TAG-CLOUD";

    //分类列表
    String CATEGORY_LIST="CATEGORY-LIST";

    //网站信息
    String AUTHOR_INFO="AUTHOR-INFO";

    //歌单列表
    String MUSIC_LIST="MUSIC-LIST";

    //公告
    String GONGGAO="GONGGAO";

    //IP封禁
    String IP_BAN="IP-BAN";

    //所有封禁的IP
    String IP_BAN_MAP="IP-BAN-MAP";

    //博客详情内容
    String BLOG_MAP="BLOG-MAP";

    //热门博客
    String HOST_BLOG_SORT="HOT-BLOG-SORT";

    //随即博客
    String RANDOM_BLOG="RANDOM-BLOG";

    //用户
    String USER="USER";

    //浏览次数
    String EYE_MAP="EYE-MAP";

    //点赞次数
    String LIKE_MAP="LIKE-MAP";

    //用户的点赞 公开
    String USER_LIKES="USER-LIKE";

    //用户的收藏 私有
    String USER_STARS="USER-STAR";

    //IP博客访问记录
    String IP_BLOG_ACCESS="IP-BLOG-ACCESS";

    //已登录用户的token
    String LOGIN_USER_TOKEN="LOGIN-USER-TOKEN";

    //token过期时间 单位:小时
    long TOKEN_EXPIRE_TIME_HOURS=5;

    //保存预览文章,5分钟过期
    String PREVIEW_BLOG="PREVIEW-BLOG";

    long PREVIEW_BLOG_EXPIRE=5;

    //用户保存的博客编写文档信息
    String USER_SAVE_BLOG_MARKDOWN="USER-SAVE-BLOG-MARKDOWN";

    /*IP限制*/

    //多少秒内
    long TIME=10;

    //在规定时间内只能访问50次
    int COUNT=10;

    //IP默认频繁访问封禁时间 单位:分钟
    int IP_BAN_TIME=10;


    /*===========================================*/

    //标签云随机多少条标签
    int RANDOM_TAG_COUNT=30;

    //标签云Redis失效时间 单位 分钟
    long TAG_CLOUD_EXPIRE=30;

    //分类Redis失效时间 单位 分钟
    long CATEGORY_EXPIRE=60;

    /*ES*/
    String ES_BLOG_INDEX="blogs";

    String ES_FILE_INDEX="files";




}
