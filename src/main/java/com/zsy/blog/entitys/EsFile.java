package com.zsy.blog.entitys;

import com.zsy.blog.common.Constants;
import com.zsy.blog.utils.DateUtils;
import com.zsy.blog.vos.FileInfoVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/20 23:22
 * @desc
 */
@Document(indexName = Constants.ES_BLOG_INDEX)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsFile {

    @Id
    private String id;

    private Integer userId;

    private String oldName;

    private String newName;

    private Date createDate;

    private long size;

    private String suffix;

    private String url;

    private String absolutePath;

    private String path;

    private boolean isPublic;

    private boolean first;



    public FileInfoVo toVo(){
        return FileInfoVo.builder()
                .md5(this.id)
                .name(this.oldName)
                .url(this.url)
                .size(this.size)
                .suffix(this.suffix)
                .dateStr(DateUtils.formatDate(this.createDate))
                .build();
    }

}
