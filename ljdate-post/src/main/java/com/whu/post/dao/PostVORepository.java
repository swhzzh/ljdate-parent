package com.whu.post.dao;

import com.whu.common.vo.PostVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVORepository extends ElasticsearchRepository<PostVO, String> {

    List<PostVO> findAllByPostId(String postId);

    void deleteByPostId(String postId);

    Page<PostVO> findAllByPosterOrderByCreateTimeDesc(String poster, Pageable pageable);
}
