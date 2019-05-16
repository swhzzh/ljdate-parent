package com.whu.user.dao;


import com.whu.common.entity.Post;
import com.whu.common.entity.User;
import com.whu.common.vo.PostVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Mapper
@Repository
public interface PostDao {

    @Insert("insert into post(post_id, snowflake_id, snowflake_id_str, area_str, category_str, title, area, address, content, poster, category, tag, max_num, cur_num," +
            " start_time, create_time) values(#{postId}, #{snowflakeId}, #{snowflakeIdStr}, #{areaStr}, #{categoryStr}, #{title}, #{area}, #{address}, #{content}, #{poster}, #{category}, #{tag}," +
            "#{maxNum}, #{curNum}, #{startTime}, #{createTime})")
    void insert(Post post);


    @Select("select p.post_id, p.snowflake_id, p.snowflake_id_str, p.title, p.area_str, p.category_str, p.area, p.address, p.content, p.poster, p.category, p.tag, p.max_num, " +
            "p.cur_num, p.images, p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno order by create_time desc ")
    List<PostVO> listVO();

    @Select("select p.post_id, p.snowflake_id, p.snowflake_id_str, p.title, p.area_str, p.category_str,p.area, p.address, p.content, p.poster, p.category, p.tag, " +
            "p.max_num, p.cur_num, p.images, p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno where p.post_id=#{postId}")
    PostVO getVOById(String postId);


    @Select("select p.post_id, p.snowflake_id, p.snowflake_id_str, p.title, p.area_str, p.category_str,p.area, p.address, p.content, p.poster, p.category, p.tag, " +
            "p.max_num, p.cur_num, p.images, p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno where p.poster=#{userId} order by create_time desc ")
    List<PostVO> getVOByUserId(String userId);

    @Select("select * from post where post_id = #{postId}")
    Post getById(String postId);

    @Select("select * from post where poster = #{userId} order by create_time desc ")
    List<Post> getByUserId(String userId);

    @Select("select * from post where poster = #{userId} and status = #{status}")
    List<Post> getByUserIdAndStatus(String userId, Integer status);

    @Update("update post set title = #{title}, content = #{content}, address = #{address}, tag = #{tag}, images = #{images}, update_time=#{updateTime} " +
            "where post_id = #{postId}")
    void update(Post post);

    @Update("update post set cur_num = cur_num + 1, update_time = #{updateTime} where post_id = #{postId} and cur_num + 1 <= max_num")
    void addNum(String postId, Timestamp updateTime);

    @Update("update post set cur_num = cur_num - 1, update_time = #{updateTime} where post_id = #{postId} and cur_num + 1 <= max_num")
    void decNum(String postId, Timestamp updateTime);

    @Update("update post set status = #{status}, update_time = #{updateTime} where post_id = #{postId}")
    void changeStatus(String postId, Integer status, Timestamp updateTime);

    @Delete("delete from post where post_id = #{postId}")
    void deleteById(String postId);

    @Delete("delete from post where poster = #{userId}")
    void clear(String userId);

    @Delete("delete from post where poster = #{userId} and status = #{status}")
    void clearByStatus(String userId, Integer status);

    @Select("select u.sno, u.snoLong, u.username, u.phone_no, u.email, u.avatar, u.credit " +
            "from post_member p join user u on p.member_id = u.sno " +
            "where p.post_id = #{postId}")
    List<User> listMember(String postId);

    @Select("select p.post_id, p.snowflake_id, p.snowflake_id_str, p.title,p.area_str, p.category_str, p.area, p.address, p.content, p.poster, p.category, p.tag, " +
            "p.max_num, p.cur_num, p.images, p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.category = #{category} " +
            "order by create_time desc ")
    List<PostVO> listVOByCategory(Integer category);

    @Select("select p.post_id, p.snowflake_id, p.snowflake_id_str,p.title,p.area_str, p.category_str, p.area,p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images,p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.area = #{area} " +
            "order by create_time desc ")
    List<PostVO> listVOByArea(Integer area);

    @Select("select p.post_id,p.snowflake_id, p.snowflake_id_str, p.title,p.area_str, p.category_str,p.area, p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images, p.start_time,p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.start_time >= #{startTime} and p.start_time <= #{endTime} " +
            "order by create_time desc ")
    List<PostVO> listVOByStartTimeBetween(Timestamp startTime, Timestamp endTime);


    @Select("select p.post_id,p.snowflake_id, p.snowflake_id_str, p.title, p.area_str, p.category_str,p.area,p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images,p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.category = #{category} and area = #{area} " +
            "order by create_time desc ")
    List<PostVO> listVOByCategoryAndArea(Integer category, Integer area);

    @Select("select p.post_id,p.snowflake_id, p.snowflake_id_str, p.title,p.area_str, p.category_str,p.area, p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images,p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.category = #{category} and p.start_time >= #{startTime} and p.start_time <= #{endTime} " +
            "order by create_time desc ")
    List<PostVO> listVOByCategoryAndStartTimeBetween(Integer category, Timestamp startTime, Timestamp endTime);

    @Select("select p.post_id,p.snowflake_id, p.snowflake_id_str, p.title,p.area_str, p.category_str, p.area,p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images,p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.area = #{area} and p.start_time >= #{startTime} and p.start_time <= #{endTime} " +
            "order by create_time desc ")
    List<PostVO> listVOByAreaAndStartTimeBetween(Integer area, Timestamp startTime, Timestamp endTime);

    @Select("select p.post_id,p.snowflake_id, p.snowflake_id_str, p.title,p.area_str, p.category_str, p.area, p.address, p.content, p.poster, p.category, p.tag, p.max_num, p.cur_num, p.images,p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno " +
            "where p.category = #{category} and area = #{area} and p.start_time >= #{startTime} and p.start_time <= #{endTime} " +
            "order by create_time desc ")
    List<PostVO> listVOByCategoryAndAreaAndStartTimeBetween(Integer category, Integer area, Timestamp startTime, Timestamp endTime);

    /**
     * 功能描述: 获得最近三天最新1000条post
     */
    @Select("select * from post where (TO_DAYS(NOW()) - TO_DAYS(create_time)<30) order by create_time desc limit 1000")
    List<Post> selectAll();

    /**
     * 功能描述: 获得最近三天最热门的5篇文章
     */
    @Select("select p.*, u.username, u.avatar " +
            "from post p join user u on p.poster = u.sno " +
            "where (TO_DAYS(NOW()) - TO_DAYS(p.create_time)<30) and p.snowflake_id in " +
            "(select t.snowflake_id from " +
            "(select snowflake_id from user_visit_action group by snowflake_id order by count(snowflake_id) desc limit 5) as t)")
    List<PostVO> getTop5();

    @Select("select p.post_id, p.snowflake_id, p.snowflake_id_str, p.title, p.area_str, p.category_str, p.area, p.address, p.content, p.poster, p.category, p.tag, p.max_num, " +
            "p.cur_num, p.images, p.start_time, p.create_time, " +
            "u.username, u.avatar " +
            "from post p join user u on p.poster=u.sno order by create_time desc limit 5")
    List<PostVO> listVO5();

    @Select("select snowflake_id from user_visit_action group by snowflake_id order by count(snowflake_id) desc limit 5")
    List<Long> getSnowflakeIds();

    @Select("select * from post where snowflake_id = #{id}")
    Post getBySnowFlakeId(Long id);

    @Select("select * from post where snowflake_id = #{snowflakeId} ")
    PostVO getBySnowflakeId(Long snowflakeId);
}
