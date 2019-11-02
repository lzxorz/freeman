package com.freeman.juc;

import cn.hutool.core.lang.Console;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 实现线程的第三种方式 Callable
 *
 * Callable与Runnable的区别:
 *  1. 可以返回值
 *  2. 可以抛异常
 *
 * FutureTask<Integer> futureTask = new FutureTask<>(Callable callable)
 * new Thread(FutureTask futureTask).start();
 */
public class TestFutureTask {
    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer> (){
            @Override
            public Integer call() throws Exception {
                Integer sum = 0;
                for (int i = 0; i < 100000000; i++) {
                    sum = sum+i;
                }
                return sum;
            }
        });
        new Thread(futureTask).start();

        try {
            // FutureTask#get方法为阻塞方法,等待线程任务返回结果, 当前线程才会继续执行
            // 也能实现闭锁的效果
            Integer integer = futureTask.get();
            Console.log("线程返回值: {}", integer);
        } catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace(); }
    }
}


