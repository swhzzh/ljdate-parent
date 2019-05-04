package com.whu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Post implements Serializable {
    private String postId;
    private Long snowflakeId;
    private String snowflakeIdStr;
    private String title;
    private String content;
    private String address;
    private Integer area; //0文理学部 1信息学部 2工学部 3医学部 4校外
    private Integer category; //0学习 1娱乐 2其他
    private String areaStr;
    private String categoryStr;

    private String poster;
    private String tag;
    private Integer maxNum;
    private Integer curNum;
    private String images;
    private Timestamp startTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer valid;
    private Integer status;
}
