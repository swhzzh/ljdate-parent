package com.whu.user.api;

import com.whu.common.entity.Post;
import com.whu.common.vo.PostVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @date 2019/5/3 15:57
 *
 */
public interface UserVisitActionApi {

    /**
     *
     *
     * @param userId
     * @return
     */
    List<PostVO> getRecommendedPosts(String userId);

}
