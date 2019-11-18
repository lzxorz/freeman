package com.freeman.common.cache.redis;

import com.freeman.common.utils.SpringContextUtil;

public class RedisLock extends AbstractLock {

    private static final String OK = "SUCCESS";
    private static final int EXPIRETIME = 10; // 过期时长


    private volatile boolean isOpenExpirationRenewal = true;
    private volatile int renewalCount = 2; // 刷新过期时间次数

    private JedisDao jedisDao;
    private String lockKey; // 执行某个操作的锁标识(唯一)
    private String lockValue; //

    public RedisLock() {this.jedisDao = SpringContextUtil.getBean(JedisDao.class);}
    public RedisLock(String lockKey, String lockValue) {
        this();
        this.lockKey = lockKey;
        this.lockValue = lockValue;
    }


    @Override
    public void lock() {
        while(true){
            /** jedis.set(lockKey, lockValue, "NX", "EX",EXPIRETIME); **/
            String result = jedisDao.set(lockKey, lockValue,EXPIRETIME);
            if(OK.equals(result)){
                //System.out.println(Thread.currentThread().getUserId()+"加锁成功!");
                //开启定时刷新过期时间
                isOpenExpirationRenewal = true;
                scheduleExpirationRenewal();
                break;
            }
            //System.out.println("线程id:"+Thread.currentThread().getUserId() + "获取锁失败，休眠5秒!时间:"+ LocalTime.now());
            //休眠1秒
            sleepBySencond(1);
        }
    }

    @Override
    public void unlock() {
        // 使用lua脚本进行原子删除操作
        String checkAndDelScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        jedisDao.getJedis().eval(checkAndDelScript, 1, lockKey, lockValue);
        // 取消增加过期时间
        isOpenExpirationRenewal = false;
    }

    /**
     * 开启定时刷新
     */
    private void scheduleExpirationRenewal(){
        new Thread(()->{
            while (isOpenExpirationRenewal && (renewalCount--)>0){
                //System.out.println("执行延迟失效时间中...");

                String checkAndExpireScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('expire',KEYS[1],ARGV[2]) " +
                        "else " +
                        "return 0 end";
                /** 刷新key的过期时间 **/
                jedisDao.getJedis().eval(checkAndExpireScript, 1, lockKey, lockValue, String.valueOf(EXPIRETIME));



                //休眠5秒
                sleepBySencond(5);
            }
        }).start();
    }

    /*public void isAccessAllowed(String uesrId, String jwt){
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return 1 else return 0 end";
        *//** 刷新key的过期时间 **//*
        Object rel = jedisDao.getJedis().eval(luaScript, 1, uesrId, jwt, "30");
    }*/

    /*public void testLock() {
        //定义线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor(0, 10, 1, TimeUnit.SECONDS,  new SynchronousQueue<>());

        //添加10个线程获取锁
        for (int i = 0; i < 10; i++) {
            pool.submit(() -> {
                try {
                    RedisLock lock = new RedisLock(new Jedis("localhost"), "lockname:test1", UUID.randomUUID().toString());
                    lock.lock();

                    //模拟业务执行15秒
                    lock.sleepBySencond(15);

                    lock.unlock();
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        }

        //当线程池中的线程数为0时，退出
        while (pool.getPoolSize() != 0) {}
    }*/


}
