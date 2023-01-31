package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.dto.FilterFileDto;
import com.zsy.blog.entitys.EsFile;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.service.FileService;
import com.zsy.blog.utils.PageUtils;
import com.zsy.blog.utils.StringUtils;
import com.zsy.blog.utils.UserUtils;
import com.zsy.blog.vos.FileInfoVo;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郑书宇
 * @create 2023/1/21 0:59
 * @desc
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private ElasticsearchRestTemplate restTemplate;

    @Override
    public PageInfo<FileInfoVo> getCurrentUserFileOrFileList(FilterFileDto filterFileDto,boolean isUser) {

        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();

        if(isUser) {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.termQuery("userId", UserUtils.getUserId()));
        }else{
            nativeSearchQueryBuilder.withQuery(QueryBuilders.termQuery("isPublic",true));
        }

        nativeSearchQueryBuilder.withPageable(PageRequest.of(filterFileDto.getPage(),filterFileDto.getSize()).previousOrFirst());

        String keyword=filterFileDto.getKeyword();

        if(!StringUtils.isEmpty(keyword)){
            if(filterFileDto.isFlag()) {
                nativeSearchQueryBuilder.withQuery(QueryBuilders.termQuery("suffix", keyword));
            }else{
                nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("oldName", keyword));
            }
        }

        if(filterFileDto.getSortType()==0) {
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("createDate").order(SortOrder.DESC));
        }else{
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("size").order(SortOrder.DESC));
        }

        SearchHits<EsFile> search = restTemplate.search(nativeSearchQueryBuilder.build(), EsFile.class, IndexCoordinates.of(Constants.ES_FILE_INDEX));

        List<FileInfoVo> fileInfoVoList=search.getSearchHits().stream().map(content->content.getContent().toVo()).collect(Collectors.toList());

        PageInfo<FileInfoVo> result = PageUtils.getBlogVoPageInfo(filterFileDto.getPage(), filterFileDto.getSize(), search.getTotalHits(), fileInfoVoList);

        return result;
    }

    @Override
    public boolean deleteFile() {
        return false;
    }
}
