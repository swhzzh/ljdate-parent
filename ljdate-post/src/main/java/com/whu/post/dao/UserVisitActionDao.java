package com.whu.post.dao;

import com.whu.common.entity.UserVisitAction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserVisitActionDao {

    @Insert("insert into user_visit_action(action_id, snowflake_id, user_id, create_time, search_keyword, " +
            "click_post_id, apply_post_id) values (#{actionId}, #{snowflakeId}, #{userId}, #{createTime}, #{searchKeyword}, " +
            "#{clickPostId}, #{applyPostId})")
    void insert(UserVisitAction uva);


    @Select("select * from user_visit_action where user_id = #{userId}")
    List<UserVisitAction> getByUserId(String userId);
}
