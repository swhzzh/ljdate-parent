package com.whu.post.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Notification;
import com.whu.common.redis.RedisService;
import com.whu.common.redis.UserKey;
import com.whu.post.api.NotificationApi;
import com.whu.post.dao.NotificationDao;
import com.whu.post.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Service
@org.springframework.stereotype.Service
public class NotificationApiImpl implements NotificationApi {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    /**
     * 发送通知
     *
     * @param notification
     */
    @Override
    public void sendNotification(Notification notification) {
        // 1.用户在线
        if (redisService.exists(UserKey.getById, notification.getReceiver())) {
            // 发送通知
            messagingTemplate.convertAndSendToUser(notification.getReceiver(), "/queue/notify", notification.getContent());
        }
        // 2.发送消息存储到数据库
        mqSender.sendNotifyMsg(notification);
    }

    /**
     * 发送用户所有未读的通知
     *
     * @param userId
     */
    @Override
    public void sendUnreadNotifications(String userId) {
        List<Notification> notifications = notificationDao.listByUserIdAndStatus(userId, 0);
        for (Notification notification : notifications) {
            messagingTemplate.convertAndSendToUser(notification.getReceiver(), "/queue/notify", notification);
        }
    }

    /**
     * 列出用户的通知
     *
     * @param userId
     * @param status -1-全部, 0-未读, 1-已读
     * @return
     */
    @Override
    public List<Notification> listByUserIdAndStatus(String userId, Integer status) {
        if (status == -1) {
            return notificationDao.listByUserId(userId);
        }
        return notificationDao.listByUserIdAndStatus(userId, status);
    }

    /**
     * 分页列出用户的通知
     *
     * @param userId
     * @param status   -1-全部, 0-未读, 1-已读
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Notification> listByPageAndUserIdAndStatus(String userId, Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notification> notifications;
        if (status == -1) {
            notifications = notificationDao.listByUserId(userId);
        } else {
            notifications = notificationDao.listByUserIdAndStatus(userId, status);
        }
        PageInfo<Notification> pageInfo = new PageInfo<>(notifications);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 删除某条通知
     *
     * @param notificationId
     */
    @Override
    public void deleteById(String notificationId) {
        notificationDao.deleteById(notificationId);
    }

    /**
     * 清空某种状态/全部的通知
     *
     * @param userId
     * @param status
     */
    @Override
    public void clearByStatus(String userId, Integer status) {
        if (status == -1){
            notificationDao.clear(userId);
        }
        else{
            notificationDao.clearByStatus(userId, status);
        }
    }
}
