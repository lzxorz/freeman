package com.freeman.juc;


import cn.hutool.core.lang.Console;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程 t1,t2,t3 交替打印99,98,97,96......1
 */
public class PrintDemo {
    public static void main(String[] args) {
        Resource r = new Resource();
        new Thread(() -> r.printNum(r.c1, r.c2), "t1").start();
        new Thread(() -> r.printNum(r.c2, r.c3), "t2").start();
        new Thread(() -> r.printNum(r.c3, r.c1), "t3").start();

    }
}

class Resource {
    private int number = 99;

    Lock lock = new ReentrantLock();
    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    Condition c3 = lock.newCondition();
    List<Condition> cs = Arrays.asList(c1, c2, c3);

    public void printNum(Condition self, Condition next) {
        lock.lock();
        try {
            while (number > 0) {
                Console.log("当前线程: {} == {}", Thread.currentThread().getName(), number--);

                next.signal();
                if (number > 1) {
                    self.await();
                }else {
                    for (Condition c : cs) {
                        if (!c.equals(self) && !c.equals(next)) {
                            c.signal();
                        }
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
}
