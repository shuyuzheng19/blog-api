package com.zsy.blog.entitys;

import com.zsy.blog.common.Constants;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * @author 郑书宇
 * @create 2023/1/18 17:18
 * @desc
 */
@Document(indexName = Constants.ES_BLOG_INDEX)
@Data @NoArgsConstructor
public class EsBlog implements Serializable {

    @Id
    private Integer id;

    @Field(type = FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_max_word")
    private String title;

    private boolean topic;

    private Integer category;

    private Integer topicId;

    private Integer uid;

    @Field(type = FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_max_word")
    private String description;

}
