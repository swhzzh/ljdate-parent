package com.whu.post.api.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.*;
import com.whu.common.exception.GlobalException;
import com.whu.common.redis.PostKey;
import com.whu.common.redis.RedisService;
import com.whu.common.redis.UserKey;
import com.whu.common.result.CodeMsg;
import com.whu.common.util.FastDFSClient;
import com.whu.common.util.SnowFlake;
import com.whu.common.util.UUIDUtil;
import com.whu.common.util.UpdateUtil;
import com.whu.common.vo.PostVO;
import com.whu.post.api.ApplicationApi;
import com.whu.post.api.PostApi;
import com.whu.post.dao.PostDao;
import com.whu.post.dao.PostMemberDao;
import com.whu.post.dao.PostVORepository;
import com.whu.post.dao.UserVisitActionDao;
import com.whu.post.rabbitmq.MQSender;
import javafx.geometry.Pos;
import jdk.nashorn.internal.objects.Global;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private UserVisitActionDao userVisitActionDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PostVORepository postVORepository;

    @Value("${img.server.url}")
    private String IMG_SERVER_URL;


    private String getCategoryStr(Integer category){
        if (category == null){
            return null;
        }
        switch (category){
            case 0:
                return "学习";
            case 1:
                return "娱乐";
            case 2:
                return "其他";
                default:
                    return "其他";
        }
    }

    private String getAreaStr(Integer area){

        if (area == null){
            return null;
        }
        switch (area){
            case 0:
                return "文理学部";
            case 1:
                return "信息学部";
            case 2:
                return "工学部";
            case 3:
                return "医学部";
            case 4:
                return "校外";
                default:
                    return "校外";
        }
    }

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
        Long snowflakeId = SnowFlake.nextId();
        post.setSnowflakeId(snowflakeId);
        post.setSnowflakeIdStr(String.valueOf(snowflakeId));
        post.setCategoryStr(getCategoryStr(post.getCategory()));
        post.setAreaStr(getAreaStr(post.getArea()));
        postDao.insert(post);

        // 2.放入缓存
        PostVO postVO = postDao.getVOById(postId);
//        postVO.setAreaStr(getAreaStr(postVO.getArea()));
//        postVO.setCategoryStr(getCategoryStr(postVO.getCategory()));

        redisService.set(PostKey.getById, postId, postVO);

        // 3.加入索引库
        postVORepository.save(postVO);

        return post;
    }

    /**
     * 为post上传图片
     *
     * @param postId
     * @return
     */
    @Override
    @Transactional
    public Post uploadImages(String postId, Map<String, byte[]> images) {
        try {
            Post post = postDao.getById(postId);
            if (post == null){
                return null;
            }
            //2、创建一个FastDFS的客户端
            // TODO: 19-5-4 存储到配置文件中
            String trackerServers = "120.79.74.63:22122";
            FastDFSClient fastDFSClient = new FastDFSClient(trackerServers);

            String imageUrls = "";
            for (Map.Entry<String, byte[]> entry : images.entrySet()) {
                String extName = entry.getKey();
                byte[] image = entry.getValue();
                //3、执行上传处理
                String path = fastDFSClient.uploadFile(image, extName);
                //4、拼接返回的url和ip地址，拼装成完整的url
                String url = IMG_SERVER_URL + path;
                imageUrls += url + ",";
            }
            post.setImages(imageUrls);
            postDao.update(post);
            Thread.sleep(100);
            // 5.更新Post
            PostVO postVO = postDao.getVOById(postId);
            redisService.set(PostKey.getById, postId, postVO);
            postVORepository.deleteByPostId(postId);
            postVORepository.save(postVO);

            return post;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据Id寻找Post
     *
     * @param postId
     * @return
     */
    @Override
    public PostVO getVOById(String postId, String userId) throws GlobalException {
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

        if (postVO == null){
            throw new GlobalException(CodeMsg.POST_NOT_EXIST);
        }

        // 4.写入缓存
        redisService.set(PostKey.getById, postId, postVO);

        // 5.记录日志
        if (userId != null){
            UserVisitAction userVisitAction = new UserVisitAction();
            userVisitAction.setActionId(UUIDUtil.uuid());
            userVisitAction.setClickPostId(postId);
            userVisitAction.setSnowflakeId(postVO.getSnowflakeId());
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
        post.setCategoryStr(getCategoryStr(post.getCategory()));
        post.setAreaStr(getAreaStr(post.getArea()));
        post.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        // 1.更新数据库
        postDao.update(post);
        // 2.更新缓存
        redisService.delete(PostKey.getById, postId);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        // 删除对应的访问记录
        userVisitActionDao.deleteByPostId(postId);
    }

    /**
     * 清空 0-正常 1-已满 2-已关闭
     *
     * @param userId
     * @param status
     */
    @Override
    public void clear(String userId, Integer status) {
        List<Post> posts = new ArrayList<>();
        if (status == -1){
            posts = postDao.getByUserId(userId);
            postDao.clear(userId);
            postVORepository.deleteByPoster(userId);
            // TODO: 19-5-4 同步redis
        }
        else {
            posts = postDao.getByUserIdAndStatus(userId, status);
            postDao.clearByStatus(userId, status);
            postVORepository.deleteByPosterAndStatus(userId, status);
        }
        for (Post post : posts) {
            String postId = post.getPostId();
            userVisitActionDao.deleteByPostId(postId);
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
     * 根据种类列举Post
     *
     * @param category
     * @param area
     * @param startTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<PostVO> listByCategoryAndAreaAndStartTime(Integer category, Integer area, Timestamp startTime, Integer pageNum, Integer pageSize) {
        PageInfo<PostVO> pageInfo = null;
        Page<PostVO> page = null;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.Direction.DESC, "createTime");
        Timestamp endTime = null;
        if (startTime != null){
            endTime = new Timestamp(startTime.getTime()+ 1000 * 60 * 60 * 24L) ;
        }


        // 全部类型
        if (category == -1){
            if (area == -1){
                if (startTime == null){
                    page = postVORepository.findAll(pageable);
                }
                else {
                    page = postVORepository.findAllByStartTimeBetweenOrderByCreateTimeDesc(startTime.getTime() ,endTime.getTime(), pageable);
                }
            }
            else {
                if (startTime == null){
                    page = postVORepository.findAllByAreaOrderByCreateTimeDesc(area, pageable);
                }
                else {
                    page = postVORepository.findAllByAreaAndStartTimeBetweenOrderByCreateTimeDesc(area, startTime.getTime(), endTime.getTime(), pageable);
                }
            }
        }
        else {
            if (area == -1){
                if (startTime == null){
                    page = postVORepository.findAllByCategoryOrderByCreateTimeDesc(category, pageable);
                }
                else {
                    page = postVORepository.findAllByCategoryAndStartTimeBetweenOrderByCreateTimeDesc(category, startTime.getTime(),endTime.getTime(), pageable);
                }
            }
            else {
                if (startTime == null){
                    page = postVORepository.findAllByCategoryAndAreaOrderByCreateTimeDesc(category, area, pageable);
                }
                else {
                    page = postVORepository.findAllByCategoryAndAreaAndStartTimeBetweenOrderByCreateTimeDesc(category, area, startTime.getTime(), endTime.getTime(), pageable);
                }
            }
        }
        if (!page.isEmpty()){
            pageInfo = new PageInfo<>(page.getContent());
            pageInfo.setPageSize(pageSize);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPages(page.getTotalPages());
        }
        // 2.搜索数据库
        else {
            PageHelper.startPage(pageNum, pageSize);
            List<PostVO> posts = postDao.listVO();
            if (category == -1){
                if (area == -1){
                    if (startTime == null){
                        posts = postDao.listVO();
                    }
                    else {
                        posts = postDao.listVOByStartTimeBetween(startTime, endTime);
                    }
                }
                else {
                    if (startTime == null){
                        posts = postDao.listVOByArea(area);
                    }
                    else {
                        posts = postDao.listVOByAreaAndStartTimeBetween(area, startTime, endTime);
                    }
                }
            }
            else {
                if (area == -1){
                    if (startTime == null){
                        posts = postDao.listVOByCategory(category);
                    }
                    else {
                        posts = postDao.listVOByCategoryAndStartTimeBetween(category, startTime, endTime);
                    }
                }
                else {
                    if (startTime == null){
                        posts = postDao.listVOByCategoryAndArea(category, area);
                    }
                    else {
                        posts = postDao.listVOByCategoryAndAreaAndStartTimeBetween(category, area, startTime, endTime);
                    }
                }
            }
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

        String[] fields = {"title", "content", "address","areaStr", "categoryStr", "tag", "username"};
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
                postDao.addNum(postId, new Timestamp(System.currentTimeMillis()));
                if (post.getCurNum() + 1 == post.getMaxNum()){
                    postDao.changeStatus(postId, 1, new Timestamp(System.currentTimeMillis()));
                }
                PostVO postVO = getVOById(postId, null);
                postVO.setCurNum(post.getCurNum() + 1);
                redisService.set(PostKey.getById, postId, postVO);
                postVORepository.deleteByPostId(postId);
                postVORepository.save(postVO);
                // 2.插入PostMember
                PostMember pm = new PostMember();
                pm.setPostId(postId);

                pm.setMemberId(application.getApplicant());
                pm.setCreateTime(new Timestamp(System.currentTimeMillis()));
                postMemberDao.insert(pm);
                // 3.更改application状态
                notification = applicationApi.handleApplication(applicationId, postId, application.getApplicant(), 1);
            }
            else {
                notification = applicationApi.handleApplication(applicationId, postId, application.getApplicant(),3);
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
     * 列出所有用户参加的post
     *
     * @param memberId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<PostVO> listPostVOByMemberId(String memberId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVO> postVOs = postDao.listVOByMemberId(memberId);
        PageInfo<PostVO> pageInfo = new PageInfo<>(postVOs);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 成员退出post
     *
     * @param memberId
     * @param postId
     */
    @Override
    @Transactional
    public void quit(String memberId, String postId) throws GlobalException{
       // 1.post成员减一
        Post post = postDao.getById(postId);
        if (post == null){
            throw new GlobalException(CodeMsg.POST_NOT_EXIST);
        }
        postDao.decNum(postId, new Timestamp(System.currentTimeMillis()));
        if (post.getCurNum().equals(post.getMaxNum())){
            postDao.changeStatus(postId, 0, new Timestamp(System.currentTimeMillis()));
        }
        PostVO postVO = getVOById(postId, null);
        postVO.setCurNum(post.getCurNum() + 1);
        redisService.set(PostKey.getById, postId, postVO);
        postVORepository.deleteByPostId(postId);
        postVORepository.save(postVO);
       // 2.删除postMember
        postMemberDao.delete(postId, memberId);
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

    /**
     * 同步数据库和索引库
     */
    @Override
    public void synchronizeDBAndIndexDB() {
        List<PostVO> postVOS = postDao.listVO();
        postVORepository.deleteAll();
        postVORepository.saveAll(postVOS);
    }
}
