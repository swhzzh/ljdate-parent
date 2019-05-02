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
import com.whu.post.dao.PostVORepository;
import com.whu.post.rabbitmq.MQSender;
import javafx.geometry.Pos;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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

    @Autowired
    private PostVORepository postVORepository;

    /**
     * 创建Post
     *
     * @param post
     */
    @Override
    public Post create(Post post) {
        // 1.插入数据库
        String postId = UUIDUtil.uuid();
        post.setPostId(postId);
        post.setCreateTime(new Timestamp(System.currentTimeMillis()));
        postDao.insert(post);

        // 2.放入缓存
        PostVO postVO = postDao.getVOById(postId);
        redisService.set(PostKey.getById, postId, postVO);

        // 3.加入索引库
        postVORepository.save(postVO);

        return post;
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
            redisService.set(PostKey.getById, postId, postVO);
            return postVO;
        }
        // 2.查索引库
        List<PostVO> postVOS = postVORepository.findAllByPostId(postId);
        if (postVOS != null && !postVOS.isEmpty()){
            postVO = postVOS.get(0);
        }
        else {
            // 3.查数据库写入索引库
            postVO = postDao.getVOById(postId);
            postVORepository.save(postVO);
        }

        // 4.写入缓存
        redisService.set(PostKey.getById, postId, postVO);

        // 5.记录日志
        if (userId != null){
            UserVisitAction userVisitAction = new UserVisitAction();
            userVisitAction.setActionId(UUIDUtil.uuid());
            userVisitAction.setClickPostId(postId);
            userVisitAction.setUserId(userId);
            userVisitAction.setCreateTime(new Timestamp(System.currentTimeMillis()));
            mqSender.sendUserVisitActionMsg(userVisitAction);
        }

        return postVO;
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

        // 3.更新索引库
        PostVO postVO = postDao.getVOById(postId);
        postVORepository.deleteByPostId(postId);
        postVORepository.save(postVO);
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
        postVORepository.deleteByPostId(postId);
    }

    /**
     * 清空 0-正常 1-已满 2-已关闭
     *
     * @param userId
     * @param status
     */
    @Override
    public void clear(String userId, Integer status) {
        if (status == -1){
            postDao.clear(userId);
        }
        else {
            postDao.clearByStatus(userId, status);
        }
    }

    /**
     * 列举某个User的post
     *
     * @param userId
     * @return
     */
    @Override
    public PageInfo<PostVO> listByUserId(String userId, Integer pageNum, Integer pageSize) {
        // 1.搜索索引库
        Page<PostVO> postVOPage = postVORepository.findAllByPosterOrderByCreateTimeDesc(userId, PageRequest.of(pageNum, pageSize));
        PageInfo<PostVO> pageInfo = null;
        if (!postVOPage.isEmpty()){
            pageInfo = new PageInfo<>(postVOPage.getContent());
            pageInfo.setPageSize(pageSize);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPages(postVOPage.getTotalPages());
        }
        // 2.搜索数据库
        else {
            PageHelper.startPage(pageNum, pageSize);
            List<PostVO> posts = postDao.getVOByUserId(userId);
            pageInfo = new PageInfo<>(posts);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
        }

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
        // 1.搜索索引库
        Page<PostVO> postVOPage = postVORepository.findAll(PageRequest.of(pageNum, pageSize, Sort.Direction.DESC, "createTime" ));
        PageInfo<PostVO> pageInfo = null;
        if (!postVOPage.isEmpty()){
            pageInfo = new PageInfo<>(postVOPage.getContent());
            pageInfo.setPageSize(pageSize);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPages(postVOPage.getTotalPages());
        }
        // 2.搜索数据库
        else {
            PageHelper.startPage(pageNum, pageSize);
            List<PostVO> posts = postDao.listVO();
            pageInfo = new PageInfo<>(posts);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
        }

        return pageInfo;
    }

    /**
     * 搜索 elasticSearch
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<PostVO> search(String keyword, Integer pageNum, Integer pageSize, String userId) {
        // 查询索引库
//        BoolQueryBuilder builder = QueryBuilders.boolQuery();
//        builder.should(QueryBuilders.fuzzyQuery("title", keyword));
//        builder.should(QueryBuilders.fuzzyQuery("content", keyword));
//        builder.should(QueryBuilders.fuzzyQuery("address", keyword));
//        builder.should(QueryBuilders.fuzzyQuery("category", keyword));
//        builder.should(QueryBuilders.fuzzyQuery("tag", keyword));
//        builder.should(QueryBuilders.fuzzyQuery("username", keyword));

        String[] fields = {"title", "content", "address", "category", "tag", "username"};
        MultiMatchQueryBuilder builder = new MultiMatchQueryBuilder(keyword, fields).minimumShouldMatch("25%");
        Page<PostVO> page = postVORepository.search(builder, PageRequest.of(pageNum, pageSize));
        //FieldSortBuilder sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
//        PageRequest pageRequest =
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        nativeSearchQueryBuilder.withQuery(builder);
//        //nativeSearchQueryBuilder.withSort(sortBuilder);
//        nativeSearchQueryBuilder.withPageable(pageRequest);
//        NativeSearchQuery query = nativeSearchQueryBuilder.build();


        PageInfo<PostVO> pageInfo = new PageInfo<>(page.getContent());
        pageInfo.setPageSize(pageSize);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPages(page.getTotalPages());

        // 记录日志
        if (userId != null){
            UserVisitAction userVisitAction = new UserVisitAction();
            userVisitAction.setActionId(UUIDUtil.uuid());
            userVisitAction.setUserId(userId);
            userVisitAction.setSearchKeyword(keyword);
            userVisitAction.setCreateTime(new Timestamp(System.currentTimeMillis()));
            mqSender.sendUserVisitActionMsg(userVisitAction);
        }
        return pageInfo;
    }

    /**
     * 增加成员
     *
     * @param applicationId
     * @param status
     */
    @Override
    @Transactional
    public Notification handleApplication(String applicationId, Integer status) {
        Application application = applicationApi.getById(applicationId);
        String postId = application.getPostId();
        Notification notification;
        if (postId == null){
            return null;
        }
        Post post = postDao.getById(postId);
        if (status == 2){
            notification =  applicationApi.handleApplication(applicationId, postId, post.getPoster(), status);
        }

        else {

            // TODO: 19-4-30 考虑使用缓存优化

            if (post.getCurNum() < post.getMaxNum()){
                // 1.增加人数
                postDao.addNum(postId, new Date());
                if (post.getCurNum() + 1 == post.getMaxNum()){
                    postDao.changeStatus(postId, 1, new Date());
                }
                PostVO postVO = getVOById(postId, null);
                postVO.setCurNum(post.getCurNum() + 1);
                redisService.set(PostKey.getById, postId, postVO);
                postVORepository.save(postVO);
                // 2.插入PostMember
                PostMember pm = new PostMember();
                pm.setPostId(postId);

                pm.setMemberId(application.getApplicant());
                pm.setCreateTime(new Timestamp(System.currentTimeMillis()));
                postMemberDao.insert(pm);
                // 3.更改application状态
                notification = applicationApi.handleApplication(applicationId, postId, post.getPoster(), 1);
            }
            else {
                notification = applicationApi.handleApplication(applicationId, postId, post.getPoster(),3);
            }
        }
        return notification;
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
