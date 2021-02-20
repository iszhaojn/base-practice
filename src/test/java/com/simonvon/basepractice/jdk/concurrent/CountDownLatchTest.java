package com.simonvon.basepractice.jdk.concurrent;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CountDownLatchTest {

    ExecutorService executorService = Executors.newFixedThreadPool(5);


    @Test
    public void test() {
        long start = System.currentTimeMillis();
        CountDownLatch count = new CountDownLatch(5);
        List<Callable<Boolean>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(() -> {
                Thread.sleep(1000);
                //将count值减1
                count.countDown();
                return true;
            });
        }

        try {
           List<Future<Boolean>> results = executorService.invokeAll(list);
           //调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
            count.await();
            results.forEach(x->{
                try {
                    System.out.println(x.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);

    }


}
