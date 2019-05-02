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
    private String title;
    private String content;
    private String address;
    private String category;
    private String poster;
    private String tag;
    private Integer maxNum;
    private Integer curNum;
    private String images;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer valid;
    private Integer status;
}
