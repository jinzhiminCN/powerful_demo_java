package jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinzhimin
 * @description: jvm参数测试
 */
public class JvmParamDemo {
    private static final Logger logger = LoggerFactory.getLogger(JvmParamDemo.class);

    private static final int SIZE_ONE_MB = 1024 * 1024;

    private static int size = 1;

    public static void showMemory() {
        double maxMemory = Runtime.getRuntime().maxMemory() / 1024.0 / 1024;
        logger.info("Max Memory:" + maxMemory + "M");

        double freeMemory = Runtime.getRuntime().freeMemory() / 1024.0 / 1024;
        logger.info("Free Memory:" + freeMemory + "M");

        double totalMemory = Runtime.getRuntime().totalMemory() / 1024.0 / 1024;
        logger.info("Total Memory:" + totalMemory + "M");
    }

    /**
     * 运行栈空间泄露
     */
    public static void stackLeak() {
        size++;
        stackLeak();
    }

    /**
     * VM参数：-verbose:gc -Xms50M -Xmx50M -XX:+PrintGCDetails -XX:+UseSerialGC -XX:SurvivorRatio=8 -Xloggc:../logs/gc.log
     */
    public static void heapOutOfMemory() {
        logger.info("5MB array allocated");
        byte[] b1 = new byte[5 * 1024 * 1024];
//        showMemory();

        logger.info("10MB array allocated");
        byte[] b2 = new byte[10 * 1024 * 1024];
//        showMemory();

        logger.info("40MB array allocated");
        byte[] b3 = new byte[40 * 1024 * 1024];
//        showMemory();
    }

    /**
     * M参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:+UseSerialGC -XX:SurvivorRatio=8 -Xloggc:../logs/gc.log
     * -Xms20M -Xmx20M -Xmn10M这3个参数确定了Java堆大小为20M，不可扩展，其中10M分配给新生代，剩下的10M即为老年代。
     * -XX:SurvivorRatio=8决定了新生代中eden与survivor的空间比例是8:1，从输出的结果也清晰的看到“eden space 8192K、
     * from space 1024K、to space 1024K”的信息，新生代总可用空间为9216K（eden+1个survivor）。
     */
    public static void testAllocation() {
        logger.info("开始分配内存");
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * SIZE_ONE_MB];
        sleep();

        logger.info("第2次分配内存");
        allocation2 = new byte[2 * SIZE_ONE_MB];
        sleep();

        logger.info("第3次分配内存");
        allocation3 = new byte[2 * SIZE_ONE_MB];
        sleep();

        logger.info("第4次分配内存");
        // 出现一次Minor GC
        allocation4 = new byte[4 * SIZE_ONE_MB];
        sleep();
    }

    public static void testAllocationGC() {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * SIZE_ONE_MB];
        allocation2 = new byte[2 * SIZE_ONE_MB];
        // 出现一次Minor GC
        allocation3 = new byte[3 * SIZE_ONE_MB];
        allocation4 = new byte[2 * SIZE_ONE_MB];
    }

    /**
     * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:+UseSerialGC -XX:SurvivorRatio=8
     * -XX:PretenureSizeThreshold=3145728  -Xloggc:../logs/gc.log
     */
    public static void testPretenureSizeThreshold() {
        byte[] allocation;
        // 直接分配在老年代中
        allocation = new byte[4 * SIZE_ONE_MB];
    }

    /**
     * VM参数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:+UseSerialGC -XX:SurvivorRatio=8
     * -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution -Xloggc:../logs/gc.log
     */
    public static void testTenuringThreshold() {
        byte[] allocation1, allocation2, allocation3;
        allocation1 = new byte[SIZE_ONE_MB / 4];
        allocation2 = new byte[4 * SIZE_ONE_MB];
        allocation3 = new byte[4 * SIZE_ONE_MB];
        allocation3 = null;
        allocation3 = new byte[4 * SIZE_ONE_MB];
    }

    public static void constantPoolOOM() {
        List<String> list = new ArrayList<String>();
        int i = 0;
        while (true) {
            // intern():若常量池没有当前的这个对象，则将此对相关加入常量池。
            list.add(String.valueOf(i++).intern());
        }
    }

    public static void sleep() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(int num) {
        try {
            Thread.sleep(num * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sleep(60);

        logger.info("第1次分配内存");
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * SIZE_ONE_MB];
        sleep();

        logger.info("第2次分配内存");
        allocation2 = new byte[2 * SIZE_ONE_MB];
        sleep();

        logger.info("第3次分配内存");
        allocation3 = new byte[2 * SIZE_ONE_MB];
        sleep();

        logger.info("第4次分配内存");
        allocation4 = new byte[4 * SIZE_ONE_MB];
        sleep();
//        heapOutOfMemory();

//        testAllocation();
//        testAllocationGC();
//        testPretenureSizeThreshold();
//        testTenuringThreshold();
//        stackLeak();
//        constantPoolOOM();

        while (true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
