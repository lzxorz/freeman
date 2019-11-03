package com.freeman.common.dataSource;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库读写分离控制
 * 主库 写入,更新,删除
 * 从库 查询
 * @author 刘志新
 */
@Slf4j
public class DynamicDataSourceKey {
    /** 数据源 常量 */
    public static final String MASTER = "MASTER"; // 主库
    public static final String SLAVE1 = "SLAVE1"; // 从库1
    public static final String SLAVE2 = "SLAVE2"; // 从库2

	private static final ThreadLocal<String> dbKey = new ThreadLocal<>();

    private static final AtomicInteger counter = new AtomicInteger(-1);




    public static String get() {
        return dbKey.get();
    }
    public static void clear() {
        dbKey.remove();
    }

    public static void master() {
        dbKey.set(MASTER); log.info("切换到master");
    }

    public static void slave() {
        if (counter.get() > 9999) counter.set(-1);
        if ((counter.getAndIncrement() % 2) == 0) {
            dbKey.set(SLAVE1); log.info("切换到==> slave1");
        }else {
            dbKey.set(SLAVE2); log.info("切换到==> slave2");
        }
    }
}
