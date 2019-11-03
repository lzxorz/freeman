package com.freeman.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class ThreadPoolDemo {

    public static void main(String[] args) throws Exception {
        fixedThreadPoolExecRunnableTask();
        // fixedThreadPoolExecCallableTask();
        // scheduledThreadPoolExecCallableTask();
    }

    // 创建固定大小的线程池, 执行 实现 Callable 接口 的线程任务, 可以延迟或定时的执行任务。
    public static void scheduledThreadPoolExecCallableTask()  throws Exception {
        //1. 创建线程池 固定数量5个, 可以延迟或定时的执行任务。
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);

        //2. 为线程池中的线程分配任务
        for (int i = 0; i < 5; i++) {
            Future<Integer> result = pool.schedule(new Callable<Integer>(){

                @Override
                public Integer call() throws Exception {
                    int num = new Random().nextInt(100);//生成随机数
                    System.out.println(Thread.currentThread().getName() + " : " + num);
                    return num;
                }

            }, 1, TimeUnit.SECONDS);

            System.out.println(result.get());
        }

        //3. 关闭线程池
        pool.shutdown();
    }


    // 创建固定大小的线程池, 执行 实现 Callable 接口 的线程任务
    public static void fixedThreadPoolExecCallableTask() throws Exception {
        //1. 创建线程池 固定数量5个
        ExecutorService pool = Executors.newFixedThreadPool(5);

        // 保存 线程任务 返回 的 Future 对象
        List<Future<Integer>> list = new ArrayList<>();

        //2. 为线程池中的线程分配任务
        for (int i = 0; i < 10; i++) {
            Future<Integer> future = pool.submit(new Callable<Integer>() { // Callable

                @Override
                public Integer call() throws Exception {
                    int sum = 0;

                    for (int i = 0; i <= 100; i++) {
                        sum += i;
                    }

                    return sum;
                }

            });

            list.add(future);
        }

        //3. 关闭线程池
        pool.shutdown();

        // 遍历 线程任务 返回 的 Future 对象
        for (Future<Integer> future : list) {
            System.out.println(future.get());
        }
    }


    // 创建固定大小的线程池, 执行 实现 Runnable 接口 的线程任务
    public static void fixedThreadPoolExecRunnableTask() {
        //1. 创建线程池 固定数量5个
        ExecutorService pool = Executors.newFixedThreadPool(5);

        //2. 为线程池中的线程分配任务
        for (int i = 0; i < 10; i++) {
            pool.submit(new Runnable() { // Runnable
                private int i = 0;

                @Override
                public void run() {
                    while(i <= 100){
                        System.out.println(Thread.currentThread().getName() + " : " + i++);
                    }
                }
            });
        }

        //3. 关闭线程池
        pool.shutdown();
    }
}
