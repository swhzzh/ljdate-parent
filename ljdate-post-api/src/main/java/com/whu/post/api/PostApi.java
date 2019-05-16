package com.whu.post.api;

import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Notification;
import com.whu.common.entity.Post;
import com.whu.common.entity.User;
import com.whu.common.exception.GlobalException;
import com.whu.common.vo.PostVO;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface PostApi {

    /**
     * 创建Post
     *
     * @param post
     */
    Post create(Post post);

    /**
     * 为post上传图片
     *
     * @return
     */
    Post uploadImages(String postId, Map<String, byte[]> images);


    /**
     * 根据Id寻找Post
     *
     * @param postId
     * @return
     */
    PostVO getVOById(String postId, String userId) throws GlobalException;

    /**
     * 更新Post
     *
     * @param post
     * @return
     */
    Post update(Post post);

    /**
     * 删除Post
     *
     * @param postId
     */
    void remove(String postId);

    /**
     * 清空 0-正常 1-已满 2-已关闭
     *
     * @param userId
     * @param status
     */
    void clear(String userId, Integer status);

    /**
     * 列举某个User的post
     *
     * @param userId
     * @return
     */
    PageInfo<PostVO> listByUserId(String userId, Integer pageNum, Integer pageSize);

    /**
     * 分页列举
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PostVO> listByPage(Integer pageNum, Integer pageSize);

    /**
     * 根据种类列举Post
     *
     * @param category
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PostVO> listByCategoryAndAreaAndStartTime(Integer category, Integer area, Timestamp startTime, Integer pageNum, Integer pageSize);

    /**
     * 搜索 solr
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PostVO> search(String keyword, Integer pageNum, Integer pageSize, String userId);

    /**
     * 增加成员
     *
     * @param applicationId
     * @param status
     */
    Notification handleApplication(String applicationId, Integer status);

    /**
     * 更改状态(人满了...)
     *
     * @param postId
     * @param status
     */
    void changeStatus(String postId, Integer status);

    /**
     * 列出post所有成员
     *
     * @param postId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<User> listMember(String postId, Integer pageNum, Integer pageSize);

    /**
     * 列出所有用户参加的post
     *
     * @param memberId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PostVO> listPostVOByMemberId(String memberId, Integer pageNum, Integer pageSize);

    /**
     * 成员退出post
     *
     * @param memberId
     * @param postId
     */
    void quit(String memberId, String postId) throws GlobalException;

    /**
     * 同步数据库和索引库
     *
     */
    void synchronizeDBAndIndexDB();
}
