package com.zsy.blog.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.BlogConditionRequest;
import com.zsy.blog.dto.BlogSortDto;
import com.zsy.blog.entitys.Blog;
import com.zsy.blog.entitys.EsBlog;
import com.zsy.blog.enums.SortEnum;
import com.zsy.blog.repository.BlogRepository;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.service.BlogService;
import com.zsy.blog.utils.Base64Utils;
import com.zsy.blog.utils.PageUtils;
import com.zsy.blog.utils.UserUtils;
import com.zsy.blog.vos.BlogVo;
import com.zsy.blog.vos.SimpleBlogVo;
import com.zsy.blog.vos.UserVo;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:06
 * @desc
 */
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final ElasticsearchRestTemplate restTemplate;

    @Override
    public Page<Blog> getBlogByPage(BlogSortDto sortRequest) {
        int page=sortRequest.getPage();
        SortEnum sort = sortRequest.getSort();
        if(sort==null) sort=SortEnum.CREATE;
        String colName=sort.colName();

        Page<Blog> result=blogRepository.findAll((root,query,build)->{

            Integer sortId=sortRequest.getSortId();

            if(sortId!=null && sortId>=0){
                return build.and(build.isNotNull(root.get("category")),build.equal(root.get("category"),sortId));
            }

            return build.isNotNull(root.get("category"));

        },PageRequest.of(page,Constants.PAGE_SIZE,Sort.by(Sort.Order.desc(colName))).previousOrFirst());

        return result;
    }

    @Override
    public Page<Blog> getTopicDocumentByPage(int documentId,int page) {
        Page<Blog> result = blogRepository.findAll((root, query, build)
                -> build.and(build.isNotNull(root.get("topic")),build.equal(root.get("topic"),documentId)
        ),PageRequest.of(page,Constants.PAGE_SIZE,Sort.by(Sort.Order.asc("createTime"))).previousOrFirst());

        return result;
    }

    @Override
    public Page<Blog> getTagBlogByPage(int tagId, int page) {
        Page<Integer> pageInfo = blogRepository.getBlogByTagId(tagId, PageRequest.of(page, Constants.PAGE_SIZE).previousOrFirst());
        List<Blog> blogs=pageInfo.stream().map(id->getBlogById(id)).collect(Collectors.toList());
        return new PageImpl<>(blogs,pageInfo.getPageable(),pageInfo.getTotalElements());
    }

    @Override
    public List<SimpleBlogVo> getRecommendBlogs() {

        List<Integer> ids= (List<Integer>) redisTemplate.opsForValue().get(Constants.RECOMMEND_KEY);

        if(ids!=null && ids.size()>0) {
            List<SimpleBlogVo> result=ids.stream().map(id->{
                Blog blog = getBlogById(id);
                return new SimpleBlogVo(blog.getId(),blog.getTitle(),blog.getCoverImage());
            }).collect(Collectors.toList());
            return result;
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public Page<Blog> getUserBlogs(int userId, int page,SortEnum sortEnum,boolean hasTopic) {
        Page<Blog> result = blogRepository.findAll((root, query, build) -> {
            Predicate predicate= build.equal(root.get("user"), userId);
            if(!hasTopic) {
                return build.and(build.isNotNull(root.get("category")),predicate);
            }
            return predicate;
        },PageRequest.of(page, Constants.PAGE_SIZE, Sort.by(Sort.Order.desc(sortEnum.colName()))).previousOrFirst());
        return result;
    }

    @Override
    public Page<Blog> searchUserBlog(int userId, int page, String keyword,boolean topic) {
        NativeSearchQueryBuilder nativeQueryBuilder=new NativeSearchQueryBuilder();

        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE).previousOrFirst();

        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("uid", userId))
                .must(QueryBuilders.termQuery("topic", topic))
                .must(QueryBuilders.multiMatchQuery(keyword, "title", "description"));

        nativeQueryBuilder
                .withQuery(query)
                .withPageable(pageable);

        SearchHits<EsBlog> search = restTemplate.search(nativeQueryBuilder.build(), EsBlog.class);

        List<Blog> blogList=search.getSearchHits().stream().map(es->getBlogById(es.getContent().getId())).collect(Collectors.toList());

        return new PageImpl<>(blogList,pageable,search.getTotalHits());
    }

    /*
        Set<Integer> ids = (Set<Integer>) redisTemplate.opsForHash().get(Constants.USER_LIKES, userId);

        if(ids==null || ids.size()==0){
            return Page.empty();
        }

        List<Blog> blogs = null;

        if(ids.size()>10) {
            blogs = ids.stream().skip((page - 1) * Constants.PAGE_SIZE).limit(Constants.PAGE_SIZE)
                    .map(id -> getBlogById(id)).collect(Collectors.toList());
        }else{
            blogs = ids.stream().map(id -> getBlogById(id)).collect(Collectors.toList());
        }
     */
    @Override
    public Page<Blog> getUserLikeBlog(int userId, int page) {

        final String KEY=Constants.USER_LIKES+":"+userId;

        if(!redisTemplate.hasKey(KEY)){

        }

        Long count = redisTemplate.opsForZSet().zCard(KEY);

        if(count==null || count==0){
            return Page.empty();
        }

        Set<Integer> ids = redisTemplate.opsForZSet().reverseRangeByScore(KEY, 0, Long.MAX_VALUE, (page - 1) * Constants.PAGE_SIZE, Constants.PAGE_SIZE);

        List<Blog> blogs=ids.stream().map(id -> getBlogById(id)).collect(Collectors.toList());

        return new PageImpl<>(blogs,PageRequest.of(page,Constants.PAGE_SIZE).previousOrFirst(),count);
    }

    @Override
    public Page<Blog> getUserStarBlog(int userId, int page) {

        final String KEY=Constants.USER_STARS+":"+userId;

        Long count = redisTemplate.opsForZSet().zCard(KEY);

        if(count==null || count==0){
            return Page.empty();
        }

        Set<Integer> ids = redisTemplate.opsForZSet().reverseRangeByScore(KEY, 0, Long.MAX_VALUE, (page - 1) * Constants.PAGE_SIZE, Constants.PAGE_SIZE);

        System.out.println(ids);

        List<Blog> blogs=ids.stream().map(id -> getBlogById(id)).collect(Collectors.toList());

        return new PageImpl<>(blogs,PageRequest.of(page,Constants.PAGE_SIZE).previousOrFirst(),count);

    }

    @Override
    public List<Blog> getUserBlogTop10(int userId) {
        Page<Blog> result = blogRepository.findAll((root, query, build) -> build.equal(root.get("user"), userId),
                PageRequest.of(1, 10, Sort.by(Sort.Order.desc(SortEnum.EYE.colName()))));
        return result.getContent();
    }

    @Override
    public Blog getBlogById(Integer id) {

        Object result = redisTemplate.opsForHash().get(Constants.BLOG_MAP, id);

        Blog blog =null;

        if(result!=null) {
            try {
                blog = objectMapper.readValue(result.toString(), Blog.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{

            blog=blogRepository.findById(id).orElseThrow(()->new GlobalException(ResultCode.NOT_FOUNT.value(),"未找到博客"));

            try {
              String  blogStr = objectMapper.writeValueAsString(blog);
              redisTemplate.opsForHash().put(Constants.BLOG_MAP,blog.getId(),blogStr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        Integer eyeCount= (Integer) redisTemplate.opsForHash().get(Constants.EYE_MAP,blog.getId());

        if(eyeCount!=null&&eyeCount>0){
            blog.setEyeCount(eyeCount);
        }

        Integer likeCount= (Integer) redisTemplate.opsForHash().get(Constants.LIKE_MAP,blog.getId());

        if(likeCount!=null&&likeCount>0){
            blog.setLikeCount(likeCount);
        }

        return blog;
    }

    @Override
    public List<Blog> batchGetBlog(List<Integer> ids) {
        return blogRepository.findAllById(ids);
    }

    @Override
    public List<SimpleBlogVo> getHotBlogs() {

        Boolean flag = redisTemplate.hasKey(Constants.HOST_BLOG_SORT);

        if(!flag) {
            Page<Blog> category = blogRepository.findAll((root, query, build) -> build.isNotNull(root.get("category"))
                    , PageRequest.of(1, 10, Sort.by(Sort.Order.desc(SortEnum.EYE.colName()))));

            List<SimpleBlogVo> result=category.getContent().stream().map(blog->
                    new SimpleBlogVo(blog.getId(),blog.getTitle(),null)).collect(Collectors.toList());

            return result;
        }

        Set<Integer> set = redisTemplate.opsForZSet().reverseRangeByScore(Constants.HOST_BLOG_SORT,0,Integer.MAX_VALUE,0,10);

        List<SimpleBlogVo> result=set.stream().map(id->{
            Blog blog = getBlogById(id);
            return new SimpleBlogVo(blog.getId(),blog.getTitle(),null);
        }).collect(Collectors.toList());

        return result;

    }

    @Override
    public List<SimpleBlogVo> randomBlog() {

        if(!redisTemplate.hasKey(Constants.RANDOM_BLOG)){

            Integer[] ids = blogRepository.findAllId();

            redisTemplate.opsForSet().add(Constants.RANDOM_BLOG,ids);

        }

        Set<Integer> set = redisTemplate.opsForSet().distinctRandomMembers(Constants.RANDOM_BLOG, 10);

        List<SimpleBlogVo> result=set.stream().map(id->{
            Blog blog = getBlogById(id);
            return new SimpleBlogVo(blog.getId(),blog.getTitle(),blog.getCoverImage());
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public PageInfo<BlogVo> searchBlogs(String keyword,int page) {
        BoolQueryBuilder should = new BoolQueryBuilder().should(QueryBuilders.multiMatchQuery(keyword, "title", "description"));

        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(should)
                .withPageable(PageRequest.of(page,Constants.PAGE_SIZE).previousOrFirst())
        .withHighlightFields(
                new HighlightBuilder.Field("title").
                        preTags("<span style='color:#ff2d51;'>").postTags("</span>"),
                new HighlightBuilder.Field("description").
                        preTags("<span style='color:#ff2d51;'>").postTags("</span>")
        );

        SearchHits<EsBlog> searchHits = restTemplate.search(nativeSearchQueryBuilder.build(), EsBlog.class, IndexCoordinates.of(Constants.ES_BLOG_INDEX));

        List<Blog> result = searchHits.getSearchHits().stream().map(r ->{

            Blog blogVo = getBlogById(r.getContent().getId());

            Map<String, List<String>> highlightFields = r.getHighlightFields();
            for (String key : highlightFields.keySet()) {
                List<String> fragments =highlightFields.get(key);
                StringBuilder sb = new StringBuilder();
                for (String fragment : fragments) {
                    sb.append(fragment);
                }
                if(key.equals("title")) {
                    blogVo.setTitle(sb.toString());
                }
                if(key.equals("description")){
                    blogVo.setDescription(sb.toString());
                }
            }
            return blogVo;
        }).collect(Collectors.toList());

        final long total=searchHits.getTotalHits();

        List<BlogVo> blogVoList = result.stream().map(blog -> blog.toBlogVo()).collect(Collectors.toList());

        return PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE, total, blogVoList);
    }

    @Override
    public PageInfo<BlogVo> searchBlogs2(String keyword, int page) {
        BoolQueryBuilder should = new BoolQueryBuilder().should(QueryBuilders.multiMatchQuery(keyword, "title", "description"));

        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(should)
                .withPageable(PageRequest.of(page,Constants.PAGE_SIZE).previousOrFirst());

        SearchHits<EsBlog> searchHits = restTemplate.search(nativeSearchQueryBuilder.build(), EsBlog.class, IndexCoordinates.of(Constants.ES_BLOG_INDEX));

        List<Blog> result = searchHits.getSearchHits().stream().map(r ->getBlogById(r.getContent().getId())).collect(Collectors.toList());

        final long total=searchHits.getTotalHits();

        List<BlogVo> blogVoList = result.stream().map(blog -> blog.toBlogVo()).collect(Collectors.toList());

        return PageUtils.getBlogVoPageInfo(page, Constants.PAGE_SIZE, total, blogVoList);
    }

    @Override
    public List<Blog> RelevantBlog(String keyword,Integer blogId) {

        MoreLikeThisQueryBuilder moreLike = new MoreLikeThisQueryBuilder(new String[]{"title","description"},new String[]{keyword},null);

        moreLike.analyzer("ik_smart");

        moreLike.minWordLength(2);

        moreLike.minTermFreq(1);

        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.withPageable(PageRequest.of(1,10).previousOrFirst());

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.mustNot(QueryBuilders.termQuery("id", blogId));

        boolQuery.should(moreLike);

        nativeSearchQueryBuilder.withQuery(boolQuery);

        List<SearchHit<EsBlog>> searchHits = restTemplate.search(nativeSearchQueryBuilder.build(), EsBlog.class, IndexCoordinates.of(Constants.ES_BLOG_INDEX)).getSearchHits();

        List<Blog> blogs=searchHits.stream().map(content->getBlogById(content.getContent().getId())).collect(Collectors.toList());

        return blogs;
    }

    @Override
    @SuppressWarnings("all")
    public void blogToEs() {

        List<Blog> blogs = blogRepository.findAll();

        List<EsBlog> esBlogList=blogs.stream().map(blog->{
            EsBlog esBlog = new EsBlog();
            esBlog.setId(blog.getId());
            esBlog.setTitle(blog.getTitle());
            esBlog.setTopic(blog.getTopic()==null?false:true);
            esBlog.setDescription(blog.getDescription());
            return esBlog;
        }).collect(Collectors.toList());

        restTemplate.save(esBlogList,IndexCoordinates.of(Constants.ES_BLOG_INDEX));

    }

    @Override
    public void blogToRedis() {
        List<Blog> blogs = blogRepository.findAll();

        for (Blog blog : blogs) {
            redisTemplate.opsForHash().put(Constants.BLOG_MAP,blog.getId(),blog);
        }
    }

    @Override
    public Page<Blog> getRangeBlog(String startTime, String endTime, int page) {

        Page<Blog> result = blogRepository.findAll((root, query, build) -> {
            query.multiselect(root.get("id"),root.get("title"),root.get("coverImage"));
            Predicate category = build.isNotNull(root.get("category"));
            Predicate createTime = build.between(root.get("createTime").as(String.class), startTime, endTime);
            return build.and(category, createTime);
        }, PageRequest.of(page, Constants.ARCHIVE_PAGE_COUNT).previousOrFirst());

        return result;
    }

    @Override
    public void savePreviewBlog(String content) {
        String username= UserUtils.getUserDetails().getUsername();
        redisTemplate.opsForValue().set(Constants.PREVIEW_BLOG+":"+username, Base64Utils.encodingToString(content),Constants.PREVIEW_BLOG_EXPIRE, TimeUnit.MINUTES);
    }

    @Override
    public String getPreviewBlog() {
        String username= UserUtils.getUserDetails().getUsername();
        String result = (String) redisTemplate.opsForValue().get(Constants.PREVIEW_BLOG + ":" + username);
        return result==null?null:Base64Utils.decodingToString(result);
    }

    @Override
    public void saveUserMarkdown(String content) {
        String username= UserUtils.getUserDetails().getUsername();
        redisTemplate.opsForHash().put(Constants.USER_SAVE_BLOG_MARKDOWN,username,Base64Utils.encodingToString(content));
    }

    @Override
    public String getSaveUserMarkdown() {
        String username= UserUtils.getUserDetails().getUsername();
        String result = (String) redisTemplate.opsForHash().get(Constants.USER_SAVE_BLOG_MARKDOWN,username);
        return result==null?"":Base64Utils.decodingToString(result);
    }

    @Override
    public boolean releaseBlog(Blog blog) {

        blog.setUser(UserUtils.getUserDetails().getUser());

        Date now = new Date();

        blog.setCreateTime(now);

        blog.setUpdateTime(now);

        blog.setEyeCount(0);

        blog.setLikeCount(0);

        Blog result = blogRepository.save(blog);

        if(result.getId()!=null) {

            redisTemplate.opsForSet().add(Constants.RANDOM_BLOG, result.getId());

            EsBlog esBlog = new EsBlog();

            esBlog.setId(result.getId());

            esBlog.setTitle(result.getTitle());

            esBlog.setTopic(result.getTopic()!=null?true:false);

            esBlog.setUid(result.getUser().getId());

            esBlog.setDescription(result.getDescription());

            restTemplate.save(esBlog,IndexCoordinates.of(Constants.ES_BLOG_INDEX));

            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean updateBlog(Blog blog) {

        UserVo user = UserUtils.getUserDetails().getUserVo();

        if(!user.isSuperAdmin()){
            if(!blog.getUser().getId().equals(user.getId())){
                throw new GlobalException(ResultCode.ERROR.value(),"只能修改自己的文章!");
            }
        }

        if(!blogRepository.existsById(blog.getId())){
            throw new GlobalException(ResultCode.NOT_FOUNT.value(),"找不到该博客!");
        }

        Blog blog2 = getBlogById(blog.getId());

        blog2.setUpdateTime(new Date());

        blog2.setContent(blog.getContent());

        blog2.setTitle(blog.getTitle());

        blog2.setDescription(blog.getDescription());

        blog2.setSourceUrl(blog.getSourceUrl());

        blog2.setTopic(blog.getTopic());

        blog2.setCategory(blog.getCategory());

        blog2.setTags(blog.getTags());

        Blog result = blogRepository.save(blog2);

        if(result.getUpdateTime().equals(blog2.getUpdateTime())) {

            redisTemplate.opsForHash().delete(Constants.BLOG_MAP,result.getId());

            EsBlog esBlog = new EsBlog();

            esBlog.setId(result.getId());

            esBlog.setTitle(result.getTitle());

            esBlog.setTopic(result.getTopic()!=null?true:false);

            esBlog.setUid(result.getUser().getId());

            esBlog.setDescription(result.getDescription());

            restTemplate.save(esBlog,IndexCoordinates.of(Constants.ES_BLOG_INDEX));

            return true;
        }else{
            return false;
        }
    }

    @Override
    public void deleteBlogByIds(List<Integer> ids) {

        if(ids==null || ids.size()==0){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(),"要删除的ID为空");
        }

        if(ids.size()>10){
            throw new GlobalException(ResultCode.ERROR.value(),"一次性最多只能删除10个!");
        }

        UserVo user = UserUtils.getUserDetails().getUserVo();

        if(user.isSuperAdmin()) {
            blogRepository.logicalDeleteBlog(ids);
        }else{
            blogRepository.logicalDeleteBlog(ids, user.getId());
        }

        redisTemplate.opsForHash().delete(Constants.BLOG_MAP,ids);


        ByQueryResponse delete = restTemplate.delete(restTemplate.idsQuery(ids.stream().map(id->String.valueOf(id)).collect(Collectors.toList())), EsBlog.class);

        System.out.println(delete);

    }

    @Override
    public Page<Blog> getTopicBlogs(Integer page,Integer topicId,String sort) {

        Page<Blog> result = blogRepository.findAll(
                (root, query, build) -> build.and(build.isNotNull(root.get("topic")),build.equal(root.get("topic"),topicId)),
                PageRequest.of(
                        page, Constants.PAGE_SIZE,
                        Sort.by(sort.equals("ORDER") ? Sort.Order.asc("createTime") : Sort.Order.desc(SortEnum.valueOf(sort).colName()))
                ).previousOrFirst());

        System.out.println(result.getContent());

        return result;
    }

    @Override
    //条件查询博客
    public Page<Blog> getConditionBlog(Integer page,BlogConditionRequest request){

        List<Predicate> list=new ArrayList<>();

        Page<Blog> result = blogRepository.findAll((root, query, build) -> {
            if (request.isFlag()) {
                if (request.getTopic() == null || request.getTopic() <= 0) {
                    throw new GlobalException(ResultCode.PARAMS_ERROR.value(), "非法的专题ID");
                }
                list.add(build.isNotNull(root.get("topic")));
                list.add(build.equal(root.get("topic"), request.getTopic()));
            } else {
                if (request.getCategory() != null && request.getCategory() > 0) {
                    list.add(build.equal(root.get("category"), request.getCategory()));
                }
            }
            return build.and(list.toArray(new Predicate[]{}));
        }, PageRequest.of(page, Constants.PAGE_SIZE,
                request.getSort().equals("ORDER") ? Sort.by(Sort.Order.asc("createTime")) : Sort.by(Sort.Order.desc(SortEnum.valueOf(request.getSort()).colName()))
        ).previousOrFirst());

        return result;
    }

    private String getSqlColName(String sort){
        if(sort.equals("CREATE")) {
            return "create_time";
        }else if(sort.equals("EYE")) {
            return "eye_count";
        }else if(sort.equals("LIKE")) {
            return "like_count";
        }else if(sort.equals("UPDATE")) {
            return "update_time";
        }else{
            return "create_time";
        }
    }

    @Override
    public Page<Blog> getDeleteBlog(Integer page, BlogConditionRequest request) {

        if(request.isFlag()) {

            if (request.getTopic() == null || request.getTopic() <= 0) {
                throw new GlobalException(ResultCode.PARAMS_ERROR.value(), "非法专题ID");
            }

            return blogRepository.getTopicDeleteBlogs(
                    PageRequest.of(page, Constants.PAGE_SIZE,
                            request.getSort().equals("ORDER") ? Sort.by(Sort.Order.asc("create_time")) : Sort.by(Sort.Order.desc(getSqlColName(request.getSort())))
                    ).previousOrFirst()
                    , request.getTopic());
        }else{
            if(request.getCategory()==null || request.getCategory() <=0) {
                return blogRepository.getDeleteBlogs(
                        PageRequest.of(page, Constants.PAGE_SIZE,
                                request.getSort().equals("ORDER") ? Sort.by(Sort.Order.asc("create_time")) : Sort.by(Sort.Order.desc(getSqlColName(request.getSort())))
                        ).previousOrFirst());
            }else{
                return blogRepository.getCategoryDeleteBlogs(
                        PageRequest.of(page, Constants.PAGE_SIZE,
                                request.getSort().equals("ORDER") ? Sort.by(Sort.Order.asc("create_time")) : Sort.by(Sort.Order.desc(getSqlColName(request.getSort())))
                        ).previousOrFirst(),request.getCategory());
            }
        }
    }

    @Override
    public boolean restoreBlog(List<Integer> ids) {

        if(ids==null || ids.size()==0){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(),"缺少要恢复博客文章的ID");
        }

        if(ids.size()>10){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(),"一次最多只能恢复10个!");
        }

        int result = blogRepository.restoreBlog(ids);

        if(result>0) {
            List<Blog> blogs = blogRepository.findAllById(ids);
            List<EsBlog> collect = blogs.stream().map(blog -> {
                EsBlog esBlog = new EsBlog();
                esBlog.setId(blog.getId());
                esBlog.setTitle(blog.getTitle());
                esBlog.setTopic(blog.getTopic() != null ? true : false);
                esBlog.setUid(blog.getUser().getId());
                esBlog.setDescription(blog.getDescription());
                return esBlog;
            }).collect(Collectors.toList());
            restTemplate.save(collect,IndexCoordinates.of(Constants.ES_BLOG_INDEX));
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean removeBlog(List<Integer> ids) {
        if(ids==null || ids.size()==0){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(),"缺少要删除博客文章的ID");
        }
        int result = blogRepository.deleteBlog(ids);

        if(result>0) {
            restTemplate.delete(ids,IndexCoordinates.of(Constants.ES_BLOG_INDEX));
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Page<Blog> likeQueryBlog(Integer page, String keyword) {
        return blogRepository.likeQueryBlog(PageRequest.of(page,Constants.PAGE_SIZE).previousOrFirst(),keyword);
    }
}
