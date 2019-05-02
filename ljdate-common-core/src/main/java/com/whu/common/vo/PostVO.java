package com.whu.common.vo;

import com.whu.common.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(indexName = "ljdate", type = "postvo")
public class PostVO implements Serializable {
    @Id
    private String id;


    private String postId;

    @Field(index = true, type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String title;
    @Field(index = true, type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String content;
    @Field(index = true, type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String address;
    @Field(index = true, type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String category;

    private String poster;
    @Field(index = true, type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String tag;

    private Integer maxNum;

    private Integer curNum;

    private String images;

    @Field(type = FieldType.Date)
    private Timestamp createTime;
    @Field(type = FieldType.Date)
    private Timestamp updateTime;

    private Integer valid;

    private Integer status;

    private String username;

    private String avatar;
}
