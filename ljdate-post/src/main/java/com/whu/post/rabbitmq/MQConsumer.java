package com.whu.post.rabbitmq;

import com.whu.common.entity.Notification;
import com.whu.common.entity.UserVisitAction;
import com.whu.common.rabbitmq.RabbitmqConfig;
import com.whu.post.dao.NotificationDao;
import com.whu.post.dao.UserVisitActionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQConsumer {

    private static final Logger log= LoggerFactory.getLogger(MQConsumer.class);


    @Autowired
    private UserVisitActionDao userVisitActionDao;

    @Autowired
    private NotificationDao notificationDao;


    /**
     * 消费存储用户操作记录的消息
     *
     * @param message
     * @throws Exception
     */
    @RabbitListener(queues = {"user-visit-action-queue"}, containerFactory = "multiListenerContainer")
    @RabbitHandler
    public void handleUserVisitActionMsg(UserVisitAction message) throws Exception {
        userVisitActionDao.insert(message);
        //log.info("");
    }

    /**
     * 消费存储用户通知的消息
     *
     * @param message
     * @throws Exception
     */
    @RabbitListener(queues = {"notify-queue"}, containerFactory = "multiListenerContainer")
    @RabbitHandler
    public void handleNotifyMsg(Notification message) throws Exception {
        notificationDao.insert(message);
        //log.info("");
    }
}
