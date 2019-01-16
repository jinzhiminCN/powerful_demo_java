package concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinzhimin
 * @description: 自命名的线程工厂
 */
public class NameThreadFactory implements ThreadFactory {
    private AtomicInteger threadNo = new AtomicInteger(1);
    private final String  nameStart;
    private final String  nameEnd  = "]";

    public NameThreadFactory(String poolName){
        nameStart = "[" + poolName + "-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = nameStart + threadNo.getAndIncrement() + nameEnd;
        Thread newThread = new Thread(runnable, threadName);
        if (newThread.getPriority() != Thread.NORM_PRIORITY) {
            newThread.setPriority(Thread.NORM_PRIORITY);
        }
        return newThread;
    }
}
