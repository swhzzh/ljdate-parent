package com.whu.post.api;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Application;
import com.whu.common.entity.Notification;

import java.util.List;
import java.util.Map;

public interface ApplicationApi {

    /**
     * 创建Application
     *
     * @param application
     */
    Map<String, Object> create(Application application);

    /**
     * 处理application
     *
     * @param applicationId
     * @param status
     */
    Notification handleApplication(String applicationId, String postId, String poster, Integer status);

    /**
     * 列出post的所有的申请
     *
     * @param postId
     * @return
     */
    PageInfo<Application> listPageByPostId(String postId, Integer pageNum, Integer pageSize);

    /**
     * 列出某人所有的申请
     *
     * @param applicant
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Application> listPageByUserId(String applicant, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 通过id获取application
     *
     * @param applicationId
     * @return
     */
    Application getById(String applicationId);

    /**
     * 删除申请
     *
     * @param applicationId
     */
    void deleteById(String applicationId);

    /**
     * 清空用户所有申请
     *
     * @param applicant
     */
    void clear(String applicant, Integer status);
}
