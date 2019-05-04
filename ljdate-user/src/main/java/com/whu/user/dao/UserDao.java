package com.whu.user.dao;

import com.whu.common.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserDao {

    @Insert("insert into user(sno, snoLong, username, password, phone_no, email, create_time) " +
            "values(#{sno}, #{snoLong}, #{username}, #{password}, #{phoneNo}, #{email}, #{createTime})")
    void insert(User user);

    @Update("update user set username = #{username}, password = #{password}, phone_no = #{phoneNo}," +
            "email = #{email}, avatar = #{avatar}, update_time = #{updateTime} where sno = #{sno}")
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
