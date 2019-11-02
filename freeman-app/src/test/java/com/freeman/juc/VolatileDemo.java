package com.freeman.juc;

import cn.hutool.core.lang.Console;

/**
 * volatile
 */
public class VolatileDemo {

    public static void main(String[] args) {

        MyRunnable myRunnable = new MyRunnable();
        new Thread(myRunnable).start();

        while (true) {
            if (myRunnable.isFlag()) {
                Console.log("Hello 我的");
                break;
            }
        }

    }
}


class MyRunnable  implements Runnable {
    private volatile boolean flag = false;

    public boolean isFlag() {
        return flag;
    }


    @Override
    public void run() {
        // try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        flag = true;
        Console.log("flag ==> true");

    }
}
