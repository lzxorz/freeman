package com.freeman.juc;

import cn.hutool.core.lang.Console;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * juc 读写锁
 * 有线程持有 写 锁的时候, 拒绝其他线程获取 读/写 锁
 * 有线程持有 读 锁的时候, 不拒绝其他线程获取 读 锁 , 并发访问 , 此时 有线程想要获取 写 锁 , 只能等这批 读 线程 执行 完成
 */
public class ReadWriteLockDemo {


    public static void main(String[] args) {
        // 使用 读写锁的 数据
        Data data = new Data();

        // 开启100个读线程, 5个写线程
        for (int i = 0; i < 105; i++) {
            // i 的值为 3/6/9/12/15 时, 创建 写线程
            // i 是其他值 创建 读线程
            if (i<16 && i%3 == 0) {
                new Thread(() -> { data.setValue(10); }, "写线程-"+i).start();
            } else {
                new Thread(() -> { data.getValue(); }, "读线程-" + i).start();
            }
        }

    }

}


class Data {
    private int value = 0;

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    // 读 数据 操作
    public int getValue() {
        lock.readLock().lock();
        try {
            Console.log("{} = {}", Thread.currentThread().getName(), value);
        }finally {
            lock.readLock().unlock();
        }
        return value;
    }

    // 写 数据 操作
    public void setValue(int value) {
        lock.writeLock().lock();
        try {
            this.value += value;
            Console.log("{} = {}", Thread.currentThread().getName(), this.value);
        }finally {
            lock.writeLock().unlock();
        }
    }
}