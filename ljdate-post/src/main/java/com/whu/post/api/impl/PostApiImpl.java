package com.whu.post.api.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.*;
import com.whu.common.redis.PostKey;
import com.whu.common.redis.RedisService;
import com.whu.common.util.UUIDUtil;
import com.whu.common.util.UpdateUtil;
import com.whu.common.vo.PostVO;
import com.whu.post.api.ApplicationApi;
import com.whu.post.api.PostApi;
import com.whu.post.dao.PostDao;
import com.whu.post.dao.PostMemberDao;
import com.whu.post.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@org.springframework.stereotype.Service
public class PostApiImpl implements PostApi {

    @Autowired
    private PostDao postDao;

    @Autowired
    MQSender mqSender;

    @Autowired
    private PostMemberDao postMemberDao;

    @Autowired
    private ApplicationApi applicationApi;

    @Autowired
    private RedisService redisService;

    /**
     * 创建Post
     *
     * @param post
     */
    @Override
    public void create(Post post) {
        // 1.插入数据库
        String postId = UUIDUtil.uuid();
        post.setPostId(postId);
        post.setCreateTime(new Timestamp(System.currentTimeMillis()));
        postDao.insert(post);

        // 2.放入缓存
        redisService.set(PostKey.getById, postId, postDao.getById(postId));

        // 3.加入索引库
        // TODO: 19-4-30
    }

    /**
     * 根据Id寻找Post
     *
     * @param postId
     * @return
     */
    @Override
    public PostVO getVOById(String postId, String userId) {
        // 1.查缓存
        PostVO postVO = redisService.get(PostKey.getById, postId, PostVO.class);
        if (postVO != null){
            redisService.set(PostKey.getById, postId, PostVO.class);
            return postVO;
        }
        // 2.查数据库并写入缓存
        PostVO post = postDao.getVOById(postId);
        redisService.set(PostKey.getById, postId, post);

        // 3.记录日志
        if (userId != null){
            UserVisitAction userVisitAction = new UserVisitAction();
            userVisitAction.setActionId(UUIDUtil.uuid());
            userVisitAction.setClickPostId(postId);
            userVisitAction.setUserId(userId);
            userVisitAction.setCreateTime(new Timestamp(System.currentTimeMillis()));
            mqSender.sendUserVisitActionMsg(userVisitAction);
        }

        return post;
    }

    /**
     * 更新Post
     *
     * @param post
     * @return
     */
    @Override
    public Post update(Post post) {
        String postId = post.getPostId();
        UpdateUtil.copyProperties(postDao.getById(postId), post);
        post.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        // 1.更新数据库
        postDao.update(post);
        // 2.更新缓存
        redisService.delete(PostKey.getById, postId);

        // TODO: 19-4-30 更新solr 
        return post;
    }

    /**
     * 删除Post
     *
     * @param postId
     */
    @Override
    public void remove(String postId) {
        postDao.deleteById(postId);
        redisService.delete(PostKey.getById, postId);
        // TODO: 19-4-30 删除solr 
    }

    /**
     * 列举某个User的post
     *
     * @param userId
     * @return
     */
    @Override
    public PageInfo<PostVO> listByUserId(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> posts = postDao.getVOByUserId(userId);
        PageInfo<PostVO> pageInfo = new PageInfo<>(posts);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 分页列举
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<PostVO> listByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> posts = postDao.listVO();
        PageInfo<PostVO> pageInfo = new PageInfo<>(posts);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 搜索 solr
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<PostVO> search(String keyword, Integer pageNum, Integer pageSize, String userId) {
        // TODO: 19-4-30 solr
        // 记录日志
        if (userId != null){
            UserVisitAction userVisitAction = new UserVisitAction();
            userVisitAction.setActionId(UUIDUtil.uuid());
            userVisitAction.setUserId(userId);
            userVisitAction.setSearchKeyword(keyword);
            userVisitAction.setCreateTime(new Timestamp(System.currentTimeMillis()));
            mqSender.sendUserVisitActionMsg(userVisitAction);
        }
        return null;
    }

    /**
     * 增加成员
     *
     * @param postId
     * @param status
     */
    @Override
    @Transactional
    public void handleApplication(String postId, String applicationId, Integer status) {
        Post post = postDao.getById(postId);
        if (status == 2){
            applicationApi.handleApplication(applicationId, postId, post.getPoster(), status);
        }

        else {

            // TODO: 19-4-30 考虑使用缓存优化

            if (post.getCurNum() < post.getMaxNum()){
                // 1.增加人数
                postDao.addNum(postId, new Date());
                if (post.getCurNum() + 1 == post.getMaxNum()){
                    postDao.changeStatus(postId, 1, new Date());
                }
                // 2.插入PostMember
                PostMember pm = new PostMember();
                pm.setPostId(postId);
                Application application = applicationApi.getById(applicationId);
                pm.setMemberId(application.getApplicant());
                pm.setCreateTime(new Timestamp(System.currentTimeMillis()));
                postMemberDao.insert(pm);
                // 3.更改application状态
                applicationApi.handleApplication(applicationId, postId, post.getPoster(), 1);
            }
            else {
                applicationApi.handleApplication(applicationId, postId, post.getPoster(),3);
            }
        }
    }

    /**
     * 列出post所有成员
     *
     * @param postId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<User> listMember(String postId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = postDao.listMember(postId);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 更改状态(人满了...)
     *
     * @param postId
     * @param status
     */
    @Override
    public void changeStatus(String postId, Integer status) {

    }
}
