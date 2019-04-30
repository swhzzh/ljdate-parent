package com.whu.post.dao;

import com.whu.common.entity.Notification;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotificationDao {

    @Insert("insert into notification(notification_id, receiver, content, application_id, post_id, create_time) " +
            "values(#{notificationId, receiver, content, applicationId, postId, createTime})")
    void insert(Notification notification);

    @Select("select * from notification where receiver = #{userId}")
    List<Notification> listByUserId(String userId);

    @Select("select * from notification where receiver = #{userId} and status = #{status}")
    List<Notification> listByUserIdAndStatus(String userId, Integer status);

    @Delete("delete from notification where notification_id = #{notificationId}")
    void deleteById(String notificationId);

    @Delete("delete from notification where receiver = #{userId}")
    void clear(String userId);

    @Delete("delete from notification where receiver = #{userId} and status = #{status}")
    void clearByStatus(String userId, Integer status);
}
