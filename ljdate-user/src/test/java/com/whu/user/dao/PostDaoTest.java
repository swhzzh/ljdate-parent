package com.whu.user.dao;


import com.whu.common.vo.PostVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PostDaoTest {
    @Autowired
    private PostDao postDao;

    @Test
    public void test(){
        for (PostVO postVO : postDao.listVO()) {
            System.out.println(postVO);
        }
        System.out.println("\n\n\n");
        for (PostVO postVO : postDao.getTop5()) {
            System.out.println(postVO);
        }
        System.out.println("\n\n\n");
        for (Long snowflakeId : postDao.getSnowflakeIds()) {
            System.out.println(snowflakeId);
        }
    }

}
