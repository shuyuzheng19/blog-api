package com.zsy.blog.manager;

import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.entitys.EsFile;
import com.zsy.blog.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 郑书宇
 * @create 2023/1/20 23:06
 * @desc
 */
@Service
public class UploadManager {

    @Value("${web.upload-path}")
    private String filePath;

    @Value("${web.hostname}")
    private String hostName;

    @Resource
    private ElasticsearchRestTemplate restTemplate;

    public String upload(MultipartFile file,boolean isPublic,Integer userId,String pathName,List<String> fileTypes,long maxSize,String sizeError,String typeError){

        long size=file.getSize();

        if(size>maxSize){
            throw new GlobalException(ResultCode.FILE_SIZE_ERROR.value(),sizeError);
        }

        String name=file.getOriginalFilename();

        String suffix = FileUtils.getFileNameSuffix(name);

        if(fileTypes!=null&&!fileTypes.contains(suffix)){
            throw new GlobalException(ResultCode.FILE_TYPE_ERROR.value(),typeError);
        }

        String fileName= UUID.randomUUID().toString()+ System.currentTimeMillis() + "." + suffix;

        String md5File=null;

        try {
            md5File = DigestUtils.md5DigestAsHex(file.getInputStream());
            EsFile result = restTemplate.get(md5File, EsFile.class, IndexCoordinates.of(Constants.ES_FILE_INDEX));
            if(result!=null){
                if(!userId.equals(result.getUserId())){
                    result.setPublic(isPublic);
                    result.setUserId(userId);
                    result.setFirst(false);
                    result.setCreateDate(new Date());
                    result.setNewName(fileName);
                    result.setOldName(name);
                }
                return result.getUrl();
            }
        } catch (IOException e) {
            throw new GlobalException(ResultCode.ERROR.value(),"解析文件MD5错误");
        }

        File path = new File(filePath+pathName);

        if(!path.exists()) path.mkdirs();

        try {
            file.transferTo(new File(path,fileName));
        } catch (IOException e) {
            throw new GlobalException(ResultCode.ERROR.value(),"上传失败!");
        }

        String url = "http://"+hostName+"/static"+pathName+"/"+fileName;

        EsFile esFile=new EsFile();

        esFile.setId(md5File);

        esFile.setUserId(userId);

        esFile.setOldName(name);

        esFile.setNewName(fileName);

        esFile.setCreateDate(new Date());

        esFile.setSize(size);

        esFile.setSuffix(suffix);

        esFile.setUrl(url);

        esFile.setAbsolutePath(path+"/"+fileName);

        esFile.setPath("/static/"+pathName+"/"+fileName);

        esFile.setPublic(isPublic);

        esFile.setFirst(true);

        restTemplate.save(esFile,IndexCoordinates.of(Constants.ES_FILE_INDEX));

        return url;

    }

}
