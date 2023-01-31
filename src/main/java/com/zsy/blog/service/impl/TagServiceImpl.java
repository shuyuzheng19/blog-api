package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.Tag;
import com.zsy.blog.repository.TagRepository;
import com.zsy.blog.service.BlogService;
import com.zsy.blog.service.TagService;
import com.zsy.blog.vos.TagVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/17 9:06
 * @desc
 */
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TagRepository tagRepository;

    @Override
    public Optional<Tag> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Set<TagVo> getTagCloud() {

        Boolean flag = redisTemplate.hasKey(Constants.TAG_CLOUD);

        if(flag) {

            Set<TagVo> tags =  redisTemplate.opsForSet().distinctRandomMembers(Constants.TAG_CLOUD,Constants.RANDOM_TAG_COUNT);

            return tags;

        }else{
            List<Tag> tags = tagRepository.findAll();

            List<TagVo> tagVoList=new ArrayList<>();

            tags.forEach(tag->{
                TagVo tagVo = tag.toTagVo();
                tagVoList.add(tagVo);
                redisTemplate.opsForSet().add(Constants.TAG_CLOUD,tagVo);
            });

            redisTemplate.expire(Constants.TAG_CLOUD,Constants.TAG_CLOUD_EXPIRE, TimeUnit.MINUTES);


            return redisTemplate.opsForSet().distinctRandomMembers(Constants.TAG_CLOUD,Constants.RANDOM_TAG_COUNT);
        }


    }

    @Override
    public List<Tag> getAllTag() {
        List<Tag> tags = tagRepository.findAll();
        return tags;
    }

    @Override
    public void saveTag(Tag tag) {

    }
}
