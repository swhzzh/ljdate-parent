package com.whu.post.dao;


import com.whu.common.entity.Post;
import com.whu.common.entity.User;
import com.whu.common.vo.PostVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface PostDao {

    @Insert("insert into post(post_id, title, address, content, poster, category, tag, max_num, cur_num," +
            " images, create_time) values(#{postId, title, address, content, poster, category, tag," +
            "max_num, cur_num, images, create_time})")
    void insert(Post post);


    @Select("select p.post_id, p.title, p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images, p.create_time, " +
            "u.username, u.avatar" +
            "from post p join user u on p.poster=u.sno ")
    List<PostVO> listVO();

    @Select("select p.post_id, p.title, p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images, p.create_time, " +
            "u.username, u.avatar" +
            "from post p join user u on p.poster=u.sno where p.post_id=#{postId}")
    PostVO getVOById(String postId);


    @Select("select p.post_id, p.title, p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images, p.create_time, " +
            "u.username, u.avatar" +
            "from post p join user u on p.poster=u.sno where p.poster=#{userId}")
    List<PostVO> getVOByUserId(String userId);

    @Select("select * from post where post_id = #{postId}")
    Post getById(String postId);

    @Select("select * from post where poster = #{userId}")
    List<Post> getByUserId(String userId);

    @Update("update post set title = #{title}, content = #{content}, address = #{address}, tag = #{tag}, update_time=#{updateTime} " +
            "where post_id = #{postId}")
    void update(Post post);

    @Update("update post set cur_num = cur_num + 1, update_time = #{updateTime} where post_id = #{postId} and cur_num + 1 <= max_num")
    void addNum(String postId, Date updateTime);

    @Update("update post set cur_num = cur_num - 1, update_time = #{updateTime} where post_id = #{postId} and cur_num + 1 <= max_num")
    void decNum(String postId, Date updateTime);

    @Update("update post set status = #{status}, update_time = #{updateTime} where post_id = #{postId}")
    void changeStatus(String postId, Integer status, Date updateTime);

    @Delete("delete from post where post_id = #{postId}")
    void deleteById(String postId);

    @Delete("delete from post where poster = #{userId}")
    void  clear(String userId);

    @Select("select u.sno, u,username, u.phone_no, u.email, u.avatar, u.credit" +
            "from post_member p join user u on p.member_id = u.sno " +
            "where p.post_id = #{postId}")
    List<User> listMember(String postId);
}
