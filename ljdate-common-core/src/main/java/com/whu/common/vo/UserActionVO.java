package com.whu.common.vo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * @date 2019/5/3 17:00
 *  某一用户的items列表以及它们的喜欢程度
 */
@NoArgsConstructor
@AllArgsConstructor
public class UserActionVO {
    private long uid;
    //用于存放关键词和它们出现的次数
    private List<String> keywords;
    private HashMap<String,Float> keyWordsPreference;
    //用于存放postid以及用户对它们的倾向度
    private HashMap<Long,Float> postPreference;

    public UserActionVO(long uid) {
        this.uid = uid;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public HashMap<String, Float> getKeyWordsPreference() {
        return keyWordsPreference;
    }

    public void setKeyWordsPreference(HashMap<String, Float> itemsPreference) {
        this.keyWordsPreference = itemsPreference;
    }

    public HashMap<Long, Float> getPostPreference() {
        return postPreference;
    }

    public void setPostPreference(HashMap<Long, Float> postPreference) {
        this.postPreference = postPreference;
    }
}
