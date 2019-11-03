package com.freeman.common.task;


import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 主要用于定时删除 Redis中 key为 freeman.user.active 中
 * 已经过期的 score
 */
@Slf4j
@Component
public class CacheTask {

    @Autowired
    private JedisDao jedisDao;

    @Scheduled(fixedRate = 3600000)
    public void run() {
        try {
            jedisDao.zremrangeByScore(Constants.USER.ACTIVE_USERS_ZSET_PREFIX, "-inf", String.valueOf(System.currentTimeMillis()));
            log.info("delete expired user");
        } catch (Exception ignore) {

        }
    }
}
