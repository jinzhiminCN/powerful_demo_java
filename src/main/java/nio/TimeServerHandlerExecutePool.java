package nio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author jinzhimin
 * @description: ${description}
 */
public class TimeServerHandlerExecutePool {
    private static final Logger logger = LoggerFactory.getLogger(TimeServerHandlerExecutePool.class);

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();

        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize,
                120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize),
                namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    }

    public void execute(java.lang.Runnable task) {
        executor.execute(task);
    }

}
