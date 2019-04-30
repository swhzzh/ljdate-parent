package com.whu.post.rabbitmq;

import com.whu.common.entity.Notification;
import com.whu.common.entity.UserVisitAction;
import com.whu.common.rabbitmq.RabbitmqConfig;
import com.whu.common.util.UUIDUtil;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送存储用户操作记录的消息
     *
     * @param message  消息
     */
    public void sendUserVisitActionMsg(UserVisitAction message) {
        CorrelationData correlationId = new CorrelationData(UUIDUtil.uuid());
        rabbitTemplate.convertAndSend(RabbitmqConfig.DIRECT_EXCHANGE, RabbitmqConfig.USER_VISIT_ACTION_ROUTINGKEY,
                message, correlationId);
    }

    /**
     * 发送存储用户通知的消息
     *
     * @param message
     */
    public void sendNotifyMsg(Notification message){
        CorrelationData correlationId = new CorrelationData(UUIDUtil.uuid());
        rabbitTemplate.convertAndSend(RabbitmqConfig.DIRECT_EXCHANGE, RabbitmqConfig.NOTIFY_ROUTINGKEY,
                message, correlationId);
    }
}
