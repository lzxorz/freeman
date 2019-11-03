package com.freeman.common.asyncTask;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Future;

/*
 * springboot 异步任务
 * 1. springboot启动类上,开启异步任务 @EnableAsync
 * 2. 编写异步任务类,注册为组件 @Component, 异步方法上加注解 @Async
 * 3. 其他组件中使用 参考 com.freeman.async.AsyncTaskTest
 *
 */

//@Async 写到类上标识这个类的所有方法都是异步的.
@Component
public class AsyncTask {
    public static Random random = new Random();

    @Async
    public void doTaskOne() {
        System.out.println("开始任务一:记录登录日志");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            random.nextInt(10000);
        }
        long end = System.currentTimeMillis();
        System.out.println("完成任务一，耗时：" + (end - start) + "毫秒");
    }

    @Async
    public void doTaskTwo() {
        System.out.println("开始任务二:记录操作日志");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            random.nextInt(10000);
        }
        long end = System.currentTimeMillis();
        System.out.println("完成任务二，耗时：" + (end - start) + "毫秒");
    }

    @Async
    public void doTaskThree() {
        System.out.println("开始任务三:暴打产品经理");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            random.nextInt(10000);
        }
        long end = System.currentTimeMillis();
        System.out.println("完成任务三，耗时：" + (end - start) + "毫秒");
    }

    @Async
    public Future<String> doTaskFour() {
        System.out.println("开始任务四:删库跑路");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            random.nextInt(10000);
        }
        long end = System.currentTimeMillis();
        System.out.println("完成任务三，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<String>("有返回值的异步任务~");
    }
}
