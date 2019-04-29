package com.whu.user.dao;

import com.whu.common.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserDao {

    @Insert("insert into user(sno, username, password, phone_no, email, create_time, update_time) " +
            "values(#{sno}, #{username}, #{password}, #{phone_no}, #{email}, #{create_time}, #{update_time})")
    void insert(User user);

    @Update("update user set username = #{username}, password = #{password}, phone_no = #{phoneNo}," +
            "email = #{email}, update_time = #{updateTime} where sno = #{sno}")
    void update(User user);

    @Delete("delete from user where sno = #{sno}")
    void delete(@Param("sno") String sno);

    @Select("select * from user where sno = #{sno}")
    User getById(@Param("sno") String sno);

    @Select("select * from user")
    List<User> list();

    @Select("select count(*) from user")
    int count();
}