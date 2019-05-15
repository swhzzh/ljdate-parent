package com.whu.user.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.whu.common.entity.Post;
import com.whu.common.entity.UserVisitAction;
import com.whu.common.localcache.LocalCache;
import com.whu.common.vo.PostVO;
import com.whu.common.vo.UserActionVO;

import com.whu.user.api.UserVisitActionApi;
import com.whu.user.dao.PostDao;
import com.whu.user.dao.UserVisitActionDao;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@org.springframework.stereotype.Service
@EnableScheduling
public class UserVisitActionApiImpl implements UserVisitActionApi {

    @Autowired
    private UserVisitActionDao userVisitActionDao;

    @Autowired
    private PostDao postDao;

    private final int RECOMMEND_POST_NUM = 5;


    @Override
    public List<PostVO> getRecommendedPosts(String userId) {
        if (userId == null){
            return postDao.getTop5();
        }

        Object cache = LocalCache.get(userId);
        if (cache == null){
            return postDao.getTop5();
        }
        // TODO: 19-5-15 数量不够 随机添加
        List<PostVO> ret = (List<PostVO>) cache;
        if (ret.size() < 5){
            int remain = 5 - ret.size();
            List<PostVO> random5 = postDao.listVO5();
            for (PostVO postVO : random5) {
                boolean dup = false;
                for (PostVO vo : ret) {
                    if (vo.getSnowflakeId().equals(postVO.getSnowflakeId())){
                        dup = true;
                        break;
                    }
                }
                if (!dup){
                    ret.add(postVO);
                    remain --;
                    if (remain == 0){
                        break;
                    }
                }
            }
        }

        return ret;
    }


    @Scheduled(initialDelay = 1000 * 30, fixedDelay = 1000 * 30)
    private void analyse() throws TasteException {
        System.out.println("run recommend");

        //list1和list2分别是按浏览（申请）帖子以及搜索所得到的actionList
        ArrayList arrayList1 = getActionsByClickOrApply();
        ArrayList arrayList2 = getActionsBySearch();

        //返回对所有计算了的用户的推荐列表
        HashMap<Long, List<RecommendedItem>> recommendedItemList1 = getRecommendedItems(arrayList1);
        HashMap<Long, List<RecommendedItem>> recommendedItemList2 = getRecommendedItems(arrayList2);
        //获取所有推荐了的userId列表
        TreeMap<Long, TreeMap<Long, Float>> treeMap = new TreeMap<>();
        for (long uid : recommendedItemList1.keySet()) {
            TreeMap<Long, Float> recommends = new TreeMap<>();
            for (RecommendedItem recommendedItem : recommendedItemList1.get(uid)) {
                recommends.put(recommendedItem.getItemID(), recommendedItem.getValue());
            }
            if (recommends != null){
                treeMap.put(uid, recommends);
            }
        }
        //将两种方式的推荐按1-1的权重计算综合推荐，选择前五推荐
        for (long uid : recommendedItemList2.keySet()) {
            TreeMap<Long, Float> recommends = treeMap.get(uid);
            for (RecommendedItem recommendedItem : recommendedItemList2.get(uid)) {
                float flag = 0;
                if (recommends.containsKey(recommendedItem.getItemID())) {
                    flag = recommends.get(recommendedItem.getItemID()) * 0.5f;
                }
                recommends.put(recommendedItem.getItemID(), flag + recommendedItem.getValue() * 0.5f);
            }
            treeMap.put(uid, recommends);
        }

        //如果推荐的post数量不足5个，则把最火的五篇帖子放进post列表中
        List<PostVO> posts = postDao.getTop5();
        if (posts.size() == 0) {
            return;
        }
        for (long uid : treeMap.keySet()) {
            TreeMap<Long, Float> recommends = treeMap.get(uid);
            if (recommends == null){
                continue;
            }
            int size = recommends.size();
            if (size < RECOMMEND_POST_NUM) {
                int times = 0;
                for (PostVO post : posts) {
                    if (!recommends.containsKey(post.getSnowflakeId())) {
                        recommends.put(post.getSnowflakeId(), 0.5f);
                        times++;
                    }
                    if (times == RECOMMEND_POST_NUM - size) {
                        break;
                    }
                }
            }
        }
        //results = new HashMap<>();
        for (long uid : treeMap.keySet()) {
            TreeMap<Long, Float> recommends = treeMap.get(uid);
            if (recommends == null){
                continue;
            }
            List<Map.Entry<Long, Float>> list = treeMapSortByValue(recommends);
            ArrayList<PostVO> resultPosts = new ArrayList<>();
            for (Map.Entry<Long, Float> aList : list) {
                resultPosts.add(postDao.getBySnowflakeId(aList.getKey()));
            }
            // 加入本地缓存, 时限5分钟
            LocalCache.put(String.valueOf(uid), resultPosts, 5, TimeUnit.MINUTES);
        }
    }


    private ArrayList getActionsByClickOrApply() {
        //获取最新1000条浏览（申请）记录
        ArrayList<UserVisitAction> actions = (ArrayList) userVisitActionDao.getActionsWhereSerachKeyWordisNull();

        //返回userActionVO的映射，以供mahout使用
        return this.getUserItemMap(actions);
    }

    private ArrayList<UserActionVO> getUserItemMap(ArrayList<UserVisitAction> actions) {

        HashMap<Long, UserActionVO> hashMap = new HashMap<>();
        for (UserVisitAction action : actions) {
            //keyword为空的前提下，snowflake不能为空
            if (action.getSnowflakeId() == null) {
                continue;
            }
            long userId = Long.valueOf(action.getUserId());
            UserActionVO userActionVO;
            HashMap<String, Float> itemPrefer;
            if (hashMap.containsKey(userId)) {
                //如果hashMap中已经有该User的ActionVO了，则修改它的itemPrefer
                userActionVO = hashMap.get(userId);
                itemPrefer = userActionVO.getKeyWordsPreference();
            } else {
                userActionVO = new UserActionVO(userId);
                itemPrefer = new HashMap<>();
            }
            String actionPostId;
            int val = 1;
            if (action.getApplyPostId() == null || "".equals(action.getApplyPostId())) {
                if (action.getClickPostId() == null || "".equals(action.getClickPostId())) {
                    continue;
                }
                actionPostId = action.getClickPostId();
            } else {
                //申请的帖子的权重是浏览帖子的三倍
                actionPostId = action.getApplyPostId();
                val = 3;
            }


            Post post = postDao.getById(actionPostId);
            if (post == null){
                continue;
            }
            String content = post.getTitle() + post.getContent();
            List<String> tags = getKeyword(content);
            for (String tag : tags) {
                if (itemPrefer.containsKey(tag)) {
                    itemPrefer.put(tag, itemPrefer.get(tag) + val);
                } else {
                    itemPrefer.put(tag, 1.0f * val);
                }
            }

            //当前得到的itemPrefer的prefer都是整数，需要百分比化
            userActionVO.setKeyWordsPreference(itemPrefer);
            hashMap.put(userId, userActionVO);
        }
        //获得三天以内的帖子
        List<Post> posts = postDao.selectAll();
        return this.calculatePreference(hashMap, posts);
    }

    private ArrayList<UserActionVO> getActionsBySearch() {
        //获取最新1000条浏览（申请）记录
        ArrayList<UserVisitAction> actions = (ArrayList) userVisitActionDao.getActionsWhereSearchKeyWordIsNotNull();

        HashMap<Long,UserActionVO> hashMap = new HashMap<>();

        for (UserVisitAction userVisitAction : actions){
            long userId = Long.valueOf(userVisitAction.getUserId());
            UserActionVO userActionVO;
            HashMap<String, Float> keyWordsPrefer;
            if (hashMap.containsKey(userId)){
                //如果hashMap中已经有该User的ActionVO了，则修改它的keywordPreference
                userActionVO = hashMap.get(userId);
                keyWordsPrefer = userActionVO.getKeyWordsPreference();
            }else {
                userActionVO = new UserActionVO(userId);
                keyWordsPrefer = new HashMap<>();
            }

            List<String> keywords = getKeyword(userVisitAction.getSearchKeyword());

            for (String keyword : keywords){
                //遇到一个keyword，则放进keywordsPrefer并对value+1
                if (keyWordsPrefer.containsKey(keyword)){
                    keyWordsPrefer.put(keyword,1.0f+keyWordsPrefer.get(keyword));
                }else {
                    keyWordsPrefer.put(keyword,1.0f);
                }
            }
            userActionVO.setKeyWordsPreference(keyWordsPrefer);
            hashMap.put(userId,userActionVO);
        }

        //获得三天以内的帖子
        List<Post> posts = postDao.selectAll();
        return this.calculatePreference(hashMap,posts);
    }


    private ArrayList<UserActionVO> calculatePreference(HashMap<Long, UserActionVO> hashMap, List<Post> posts) {
        //当前userActionVO里面的itemPreference存储的item是关键词，需要转换为对应的postId
        ArrayList<UserActionVO> userActionVOs = new ArrayList<>();
        for (long uid : hashMap.keySet()) {
            UserActionVO userActionVO = hashMap.get(uid);
            //hashMap避免读取了重复的post
            HashMap<String, Float> itemPrefer = userActionVO.getKeyWordsPreference();
            HashMap<Long, Float> postPrefer = new HashMap<>();
            float total = 0;
            for (String item : itemPrefer.keySet()) {
                total += itemPrefer.get(item);
            }
            //当前item是关键词
            for (String item : itemPrefer.keySet()) {
                //权重是从1到5排列的float数据
                float prefer = itemPrefer.get(item) / total * 4 + 1;
                //权重小于1.5的去掉
                if (prefer - 1.5 > 0) {
                    //将权值和对应的itemId号填入postPrefer
                    for (Post post : posts) {
                        String content = post.getTitle() + " " + post.getContent();
                        //除开自己的帖子不推荐
                        if (Long.valueOf(post.getPoster()) == uid) {
                            continue;
                        }
                        if (content.contains(item)) {
                            float add = 0f;
                            if (post.getContent().contains(item)) {
                                add += 0.4;
                            }
                            if (post.getTitle().contains(item)) {
                                add += 0.4;
                            }
                            //喜欢程度最大不能超过5
                            postPrefer.put(post.getSnowflakeId(), Math.min(prefer + add, 5f));
                        }
                    }
                }
                userActionVO.setPostPreference(postPrefer);
            }
            userActionVOs.add(userActionVO);
        }
        return userActionVOs;
    }



    private HashMap<Long, List<RecommendedItem>> getRecommendedItems(ArrayList<UserActionVO> userActionVOs) throws TasteException {
        FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();
        int num = 0;
        for (UserActionVO userActionVO : userActionVOs) {
            PreferenceArray preference = new GenericUserPreferenceArray(userActionVO.getPostPreference().size());
            preference.setUserID(num, userActionVO.getUid());
            int i = 0;
            for (long postId : userActionVO.getPostPreference().keySet()) {
                preference.setItemID(i, postId);
                //结果保留一位小数
                float val = Float.parseFloat(new DecimalFormat("#.0").format(userActionVO.getPostPreference().get(postId)));
                preference.setValue(i, val);
                i++;
            }

            preferences.put(userActionVO.getUid(), preference);
            num++;
        }
        DataModel model = new GenericDataModel(preferences);

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        //为每个用户推荐五个post
        HashMap<Long, List<RecommendedItem>> hashMap = new HashMap<>();
        for (UserActionVO userActionVO : userActionVOs) {
            long userId = userActionVO.getUid();
            hashMap.put(userId, recommender.recommend(userId, 5));
        }

        return hashMap;

    }




    private List<Map.Entry<Long, Float>> treeMapSortByValue(Map<Long, Float> map) {
        List<Map.Entry<Long, Float>> list = new ArrayList<Map.Entry<Long, Float>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Long, Float>>() {
            @Override
            public int compare(Map.Entry<Long, Float> o1, Map.Entry<Long, Float> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        return list;
    }


    private List<String> getKeyword(String content) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        return segmenter.sentenceProcess(content);
    }

}
