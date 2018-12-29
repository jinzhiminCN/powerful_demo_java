package jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author jinzhimin
 * @description: 自定义类加载器
 */
public class MyClassLoader extends ClassLoader {
    private static final Logger logger = LoggerFactory.getLogger(MyClassLoader.class);

    private String root;

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    private byte[] loadClassData(String className) {
        String fileName = root + File.separatorChar
                + className.replace('.', File.separatorChar) + ".class";
        try {
            InputStream ins = new FileInputStream(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            while ((length = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public static void main(String[] args) {
        MyClassLoader classLoader = new MyClassLoader();
        classLoader.setRoot("E:\\temp");

        Class<?> testClass = null;
        try {
            testClass = classLoader.loadClass("jvm.Test2");
            Object object = testClass.newInstance();
            logger.info(object.getClass().getClassLoader().toString());
        } catch (ClassNotFoundException e) {
            logger.info("类文件未找到！", e);
        } catch (InstantiationException e) {
            logger.info("实例化异常！", e);
        } catch (IllegalAccessException e) {
            logger.info("非法访问异常！", e);
        }
    }
}
