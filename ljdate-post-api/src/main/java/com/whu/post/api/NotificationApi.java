package com.whu.post.api;

import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Notification;

import java.util.List;

public interface NotificationApi {

    /**
     * 发送通知
     *
     * @param notification
     */
    void sendNotification(Notification notification);

    /**
     * 列出用户的通知
     *
     * @param userId
     * @param status 0-未读, 1-已读
     * @return
     */
    List<Notification> listByUserIdAndStatus(String userId, Integer status);

    /**
     * 发送用户所有未读的通知
     *
     * @param userId
     */
    void sendUnreadNotifications(String userId);

    /**
     * 分页列出用户的通知
     *
     * @param userId
     * @param status 0-未读, 1-已读
     * @param pageNum
     * @param pageSizew
     * @return
     */
    PageInfo<Notification> listByPageAndUserIdAndStatus(String userId, Integer status, Integer pageNum, Integer pageSize);


    /**
     * 删除某条通知
     *
     * @param notificationId
     */
    void deleteById(String notificationId);

    /**
     * 清空某种状态/全部的通知
     *
     * @param userId
     * @param status
     */
    void clearByStatus(String userId, Integer status);
}
