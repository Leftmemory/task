package com.zxd.task.executor;

import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zxd
 * @since 16/11/17.
 */
@Service
public class ScheduleTask {
    private ScheduledExecutorService executor = java.util.concurrent.Executors.newScheduledThreadPool(2);


    public void execute() {
        executor.execute(new RunTask());
    }


    public static class RunTask implements Runnable {

        @Override
        public void run() {
            System.out.println("----- run");

            try {
                wait(1000);
            }catch (Exception e){
                throw new RuntimeException("333");
            }
            throw new RuntimeException("333");
        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService executorService = java.util.concurrent.Executors.newScheduledThreadPool(1);
        RunTask task = new RunTask();
        //1秒后开始执行任务，以后每隔2秒执行一次
//        executorService.scheduleWithFixedDelay(task, 1000, 2000, TimeUnit.MILLISECONDS);
        executorService.schedule(task,1L,TimeUnit.SECONDS);
        executorService.schedule(task,1L,TimeUnit.SECONDS);
        executorService.schedule(task,1L,TimeUnit.SECONDS);
    }
}
