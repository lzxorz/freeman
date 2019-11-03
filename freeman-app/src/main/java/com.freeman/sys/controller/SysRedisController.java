package com.freeman.sys.controller;

import com.freeman.common.result.R;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.cache.redis.RedisInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/redis")
public class SysRedisController {

    @Autowired
    private JedisDao jedisDao;

    /** Redis详细信息 */
    @GetMapping("info")
    public R getRedisInfo() {
        List<RedisInfo> infoList = jedisDao.getRedisInfo();
        return R.ok(infoList);
    }

    /** Redis key数量 */
    @GetMapping("keysSize")
    public Map<String, Object> getKeysSize() {
        return jedisDao.getKeysSize();
    }

    /** Redis内存占用情况 */
    @GetMapping("memoryInfo")
    public Map<String, Object> getMemoryInfo() {
        return jedisDao.getMemoryInfo();
    }
}
