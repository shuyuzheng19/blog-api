package com.zsy.blog.service;

import com.zsy.blog.dto.FilterFileDto;
import com.zsy.blog.response.PageInfo;
import com.zsy.blog.vos.FileInfoVo;

/**
 * @author 郑书宇
 * @create 2023/1/21 0:55
 * @desc
 */
public interface FileService {
    PageInfo<FileInfoVo> getCurrentUserFileOrFileList(FilterFileDto filterFileDto,boolean isUser);

    boolean deleteFile();
}
