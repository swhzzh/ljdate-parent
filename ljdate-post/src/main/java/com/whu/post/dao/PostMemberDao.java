package com.whu.post.dao;

import com.whu.common.entity.PostMember;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PostMemberDao {

    @Insert("insert into post_member(post_id, member_id, create_time) " +
            "values(#{postId}, #{memberId}, #{createTime})")
    void insert(PostMember postMember);

    @Delete("delete from post_member where post_id = #{postId} and member_id = #{memberId}")
    void delete(String postId, String memberId);
}
