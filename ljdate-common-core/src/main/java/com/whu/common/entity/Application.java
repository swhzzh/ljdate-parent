package com.whu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Application implements Serializable {
    private String applicationId;
    private String applicant;
    private String postId;
    private String phoneNo;
    private String wechatNo;
    private String qqNo;
    private String email;
    private String content;
    private Integer status;
    private Integer valid;
    private Timestamp createTime;
    private Timestamp updateTime;
}
