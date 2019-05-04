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
public class Notification implements Serializable {

    private String notificationId;
    private String receiver;
    private String content;
    private String applicationId;
    private String postId;
    private Integer type;
    private Integer status;
    private Integer valid;
    private Timestamp createTime;
    private Timestamp updateTime;
}
