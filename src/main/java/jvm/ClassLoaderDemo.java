package jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GsonUtil;

/**
 * @author jinzhimin
 * @description: 测试classloader类加载器
 */
public class ClassLoaderDemo {
    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderDemo.class);

    public static void showClassLoader(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        System.out.println(loader);
        System.out.println(loader.getParent());
        System.out.println(loader.getParent().getParent());

        logger.info(loader.toString());
//        logger.info(GsonUtil.getGson().toJson(loader));
        logger.info(loader.getParent().toString());
    }

    public static void testLoadClass(){
        ClassLoader loader = ClassLoaderDemo.class.getClassLoader();
        logger.info(loader.toString());

        try {
            // 使用ClassLoader.loadClass()来加载类，不会执行初始化块
            loader.loadClass("jvm.Test2");
            // 使用Class.forName()来加载类，默认会执行初始化块
            Class.forName("jvm.Test2");
            // 使用Class.forName()来加载类，并指定ClassLoader，初始化时不执行静态块
            Class.forName("jvm.Test2", false, loader);
        } catch (ClassNotFoundException e) {
            logger.info("未找到类文件！", e);
        }
    }

    public static void main(String[] args) {
        showClassLoader();
//        testLoadClass();
    }
}
