package com.bilibili.Service;

import com.bilibili.data.dao.CommonDAO;
import com.bilibili.data.manager.MasterManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * describe :
 * author : xusong
 * createTime : 2018/4/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GirlServiceTest {
    @Autowired
    private CommonDAO commonDAO;
    @Autowired
    private MasterManager masterManager;
}