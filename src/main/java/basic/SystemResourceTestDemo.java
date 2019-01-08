package basic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * @author jinzhimin
 * @description: 系统资源测试。包括CPU，磁盘，内存等。
 */
public class SystemResourceTestDemo {
    private static final Logger logger = LoggerFactory.getLogger(SystemResourceTestDemo.class);

    public static class HoldCpuTask implements Runnable {
        public static Object[] lock = new Object[100];
        public static Random intRandom = new Random();

        static {
            for (int i = 0; i < lock.length; i++) {
                lock[i] = new Object();
            }
        }

        @Override
        public void run() {
            int loop = 0;
            while (true) {
                // 随机占用CPU资源
                int loopNum = intRandom.nextInt(100);

                int[] array = new int[loopNum];
                for (int i = 0; i < loopNum; i++) {
                    array[i] = intRandom.nextInt(100);
                }

                // 开始冒泡方式排序
                for (int i = 0; i < loopNum; i++) {
                    for (int j = 0; j < loopNum - 1; j++) {
                        // 比较相邻元素
                        if (array[j] > array[j + 1]) {
                            int temp;
                            temp = array[j];
                            array[j] = array[j + 1];
                            array[j + 1] = temp;
                        }
                    }
                }

                // 随机占用磁盘IO
                int fileLoop = intRandom.nextInt(10000);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File("temp"));
                    for (int i = 0; i < fileLoop; i++) {
                        fos.write(i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(new File("temp"));
                    while (fis.read() != -1) {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // 随机开始持有锁
                int x = intRandom.nextInt(100);
                synchronized (lock[x]) {
                    if (x % 2 == 0) {
                        try {
                            lock[x].wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        lock[x].notifyAll();
                    }
                }

                // 随机开始占用内存
                int memSize = intRandom.nextInt(100);
                Vector<byte[]> vector = new Vector<>();

                for (int i = 0; i <= 10; i++) {
                    byte[] bytes = new byte[memSize * memSize];
                    vector.add(bytes);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int threadNum = new Random().nextInt(100);
        logger.info(threadNum + "");

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(threadNum, threadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < threadNum; i++) {
            singleThreadPool.execute(new HoldCpuTask());
        }
        singleThreadPool.shutdown();
    }


}
