package com.whu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserVisitAction implements Serializable {
    private String actionId;
    private String userId;
    private Timestamp createTime;
    private String searchKeyword;
    private String clickPostId;
    private String applyPostId;
    private Integer valid;
}
