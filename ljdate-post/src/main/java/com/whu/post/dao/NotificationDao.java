package com.whu.post.dao;

import com.whu.common.entity.Notification;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotificationDao {

    @Insert("insert into notification(notification_id, receiver, content, type, application_id, post_id, create_time) " +
            "values(#{notificationId}, #{receiver}, #{content}, #{type}, #{applicationId}, #{postId}, #{createTime})")
    void insert(Notification notification);

    @Select("select * from notification where receiver = #{userId} order by create_time DESC")
    List<Notification> listByUserId(String userId);

    @Select("select * from notification where receiver = #{userId} and status = #{status} order by create_time DESC")
    List<Notification> listByUserIdAndStatus(String userId, Integer status);

    @Select("select * from notification where notification_id = #{notificationId} ")
    Notification getById(String notificationId);

    @Delete("delete from notification where notification_id = #{notificationId}")
    void deleteById(String notificationId);

    @Delete("delete from notification where receiver = #{userId}")
    void clear(String userId);

    @Delete("delete from notification where receiver = #{userId} and status = #{status}")
    void clearByStatus(String userId, Integer status);

    @Update("update notification set status = #{status} where notification_id = #{notificationId}")
    void updateByStatus(String notificationId, Integer status);
}
