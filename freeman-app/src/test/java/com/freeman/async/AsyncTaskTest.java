package com.freeman.async;

import com.freeman.common.asyncTask.AsyncTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.Future;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncTaskTest {

    @Autowired
    private AsyncTask anysc;

    @Test
    public void asyncTest(){
        try {
            for (int i = 0; i < 10000000; i++) {
                new Random().nextInt(10000);
            }
            System.out.println("***************开始上课...**************");
            anysc.doTaskOne();
            anysc.doTaskTwo();
            anysc.doTaskThree();
            Future<String> future = anysc.doTaskFour();
            while (!future.isDone()){ }
            if (future.isDone()) {
                System.out.println("异步返回: "+future.get());
            }
            System.out.println("***************终于下课了...**************");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
