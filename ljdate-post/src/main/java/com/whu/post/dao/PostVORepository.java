package com.whu.post.dao;

import com.whu.common.vo.PostVO;
import javafx.geometry.Pos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PostVORepository extends ElasticsearchRepository<PostVO, String> {

    List<PostVO> findAllByPostId(String postId);

    void deleteByPostId(String postId);

    void deleteByPoster(String poster);

    void deleteByPosterAndStatus(String poster, Integer status);

    Page<PostVO> findAllByPosterOrderByCreateTimeDesc(String poster, Pageable pageable);

    Page<PostVO> findAllByCategoryOrderByCreateTimeDesc(Integer category, Pageable pageable);

    Page<PostVO> findAllByAreaOrderByCreateTimeDesc(Integer area, Pageable pageable);

    Page<PostVO> findAllByStartTimeBetweenOrderByCreateTimeDesc(Long startTime, Long endTime, Pageable pageable);

    Page<PostVO> findAllByCategoryAndAreaOrderByCreateTimeDesc(Integer category, Integer area, Pageable pageable);

    Page<PostVO> findAllByCategoryAndStartTimeBetweenOrderByCreateTimeDesc(Integer category, Long startTime, Long endTime, Pageable pageable);

    Page<PostVO> findAllByAreaAndStartTimeBetweenOrderByCreateTimeDesc(Integer area, Long startTime, Long endTime, Pageable pageable);

    Page<PostVO> findAllByCategoryAndAreaAndStartTimeBetweenOrderByCreateTimeDesc(Integer category, Integer area, Long startTime, Long endTime, Pageable pageable);
}
