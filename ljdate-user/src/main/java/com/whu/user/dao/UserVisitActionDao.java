package com.whu.user.dao;

import com.whu.common.entity.UserVisitAction;
import org.apache.ibatis.annotations.Delete;
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

    @Select("select * from user_visit_action where user_id = #{userId} order by create_time limit 10")
    List<UserVisitAction> getByUserIdLimitNewest10(long userId);

    @Select("select * from user_visit_action where search_keyword IS NULL and (TO_DAYS(NOW()) - TO_DAYS(create_time)<30) order by create_time desc limit 1000")
    List<UserVisitAction> getActionsWhereSerachKeyWordisNull();

    @Select("select * from user_visit_action where search_keyword IS NOT NULL and (TO_DAYS(NOW()) - TO_DAYS(create_time)<30) order by create_time desc limit 1000")
    List<UserVisitAction> getActionsWhereSearchKeyWordIsNotNull();

    @Delete("delete * from user_visit_action where click_post_id = #{postId} or apply_post_id = #{postId} ")
    void deleteByPostId(String postId);

    @Delete("delete * from user_visit_action where ")
    void deleteByUserId(String userId);
}
