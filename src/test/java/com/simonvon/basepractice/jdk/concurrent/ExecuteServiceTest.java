package com.simonvon.basepractice.jdk.concurrent;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecuteServiceTest {


    @Test
    public void testRejectPolicy() {
        ThreadPoolExecutor es = new ThreadPoolExecutor(
                2,
                2,
                2,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                //主线程运行拒绝的任务，实现负反馈
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        //允许核心线程池过期
        es.allowCoreThreadTimeOut(true);
        //创建线程池后默认不创建线程，只有任务进来的时候才创建
        es.execute(new MyTask("1"));
        es.execute(new MyTask("2"));
        es.execute(new MyTask("3"));
        es.execute(new MyTask("4"));
        es.execute(new MyTask("5"));
        es.execute(new MyTask("6"));
        es.execute(new MyTask("7"));

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //超过keepAliveTime的时间限制后，线程池会销毁超时等待的线程
        System.out.println("ActiveCount" + es.getActiveCount());
        System.out.println("CorePoolSize" + es.getCorePoolSize());
        es.execute(new MyTask("8"));

    }

    static class MyTask implements Runnable {

        private final String id;

        public MyTask(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ":task:" + id);
        }
    }


}
