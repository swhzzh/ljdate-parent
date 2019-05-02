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
public class PostMember implements Serializable {
    private String postId;
    private String memberId;
    private Integer valid;
    private Timestamp createTime;
    private Timestamp updateTime;
}
