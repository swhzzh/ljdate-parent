package com.whu.post.dao;

import com.whu.common.entity.Application;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface ApplicationDao {

    @Insert("insert into application(application_id, applicant, post_id, phone_no, wechat_no, qq_no, " +
            "email, content, create_time) values (#{applicationId}, #{applicant}, #{postId}, #{phoneNo}, #{wechatNo}, #{qqNo}, " +
            "#{email}, #{content}, #{createTime})")
    void insert(Application application);

    @Update("update application set status = #{status}, update_time = #{updateTime} where application_id = #{applicationId}")
    void changeStatus(String applicationId, Integer status, Date updateTime);

    @Select("select * from application where applicant = #{userId} order by create_time DESC")
    List<Application> listByUserId(String userId);

    @Select("select * from application where applicant = #{userId} and status = #{status} order by create_time DESC")
    List<Application> listByUserIdAndStatus(String userId, Integer status);

    @Select("select * from application where post_id = #{postId} order by create_time DESC")
    List<Application> listByPostId(String postId);

    @Select("select * from application where post_id = #{postId} and applicant = #{applicant}")
    Application getByPostIdAndApplicant(String postId, String applicant);

    @Select("select * from application where application_id = #{applicationId}")
    Application getById(String applicationId);

    @Delete("delete from application where application_id = #{applicationId}")
    void deleteById(String applicationId);

    @Delete("delete from application where applicant = #{applicant}")
    void deleteByApplicant(String applicant);

    @Delete("delete from application where applicant = #{applicant} and status = #{status}")
    void deleteByApplicantAndStatus(String applicant, Integer status);
}
