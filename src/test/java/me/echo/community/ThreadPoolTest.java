package me.echo.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    // JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK可执行定时任务线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // Spring可执行定时任务线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private void sleep(int m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testExecutorService(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello testExecutorService");
            }
        };

        for (int i = 0; i <= 10; i++) {
            executorService.submit(runnable);
        }
        sleep(10000);
    }


    @Test
    public void testScheduledExecutorService(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello testScheduledExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(runnable, 1000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }

    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello testThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.execute(runnable);
        }
        sleep(10000);
    }


    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello testThreadPoolTaskScheduler");
            }
        };

        Date start = new Date(System.currentTimeMillis()+10000);
        taskScheduler.scheduleAtFixedRate(runnable, start, 1000);
        sleep(30000);
    }

    @Async
    public void execute1(){
        logger.debug("execute1");
    }

    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for (int i = 0; i < 10; ++i) {
          this.execute1(); // x
        }
        sleep(10000);
    }

    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2(){
        logger.debug("execute2");
    }

    @Test
    public void testThreadPoolTaskSchedulerSimple(){
        sleep(30000);
    }


}
