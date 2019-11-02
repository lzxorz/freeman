package com.freeman.common.runner;

import com.freeman.sys.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Order
@Slf4j
@Component
public class StartedUpRunner implements ApplicationRunner/*, CommandLineRunner*/ {

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("Redis连接中 ······");
            cacheService.testConnect();

            log.info("缓存初始化 ······");

            log.info("缓存用户数据 ······");
            cacheService.loadCaches();

            if (context.isActive()) {
                log.info("Freeman 启动完毕, 时间: " + LocalDateTime.now());
            }

        } catch (Exception e) {
            log.error("缓存初始化失败，{}", e.getMessage());
            log.error("启动失败......................");
            context.close();
        }
    }
}
