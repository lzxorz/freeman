package com.freeman.spring.data.utils.idgenerate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * 生成ID算法工具类.
 */
public class IdGen implements IdentifierGenerator {
    public static final String TYPE = "com.freeman.spring.data.utils.idgenerate.IdGen";

    private static final SecureRandom random = new SecureRandom();
    private static final IdWorker idWorker = new IdWorker(-1, -1);


    /** 生成UUID, 中间无-分割 */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /** 使用SecureRandom随机生成Long */
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }

    /** 使用snowflake生成18位唯一编号 */
    public static long snowflake() {
        return idWorker.nextId();
    }

    /**
     * 获取新唯一编号（18位数值）
     * 来自于twitter项目snowflake的id产生方案，全局唯一，时间有序。
     * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return idWorker.nextId();
    }

}



