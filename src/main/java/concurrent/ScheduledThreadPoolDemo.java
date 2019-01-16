package concurrent;

import task.LogTask;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author jinzhimin
 * @description: 定时调度线程池使用
 */
public class ScheduledThreadPoolDemo {

    public static void testLogThread(){
        // 线程工厂
        ThreadFactory threadFactory = new NameThreadFactory("日志线程池");
        // 线程拒绝策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(20, threadFactory, handler);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 52);
        Date date = calendar.getTime();

        executor.schedule(new LogTask("第1个日志任务"), 2, TimeUnit.SECONDS);
        executor.schedule(new LogTask("第2个日志任务"), date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
//        executor.scheduleWithFixedDelay(new LogTask("第2个日志任务"), date.getTime() - System.currentTimeMillis(), 2, TimeUnit.SECONDS);

    }

    public static void main(String[] args) {

    }
}
