package task;

import netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: 打印日志的任务
 */
public class LogTask implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(LogTask.class);

    private String name;

    public LogTask(String name){
        this.name = name;
        Thread.currentThread().setName(name);
    }

    @Override
    public void run() {
        System.out.println("日志名称：" + name + ", 线程名称：" + Thread.currentThread().getName());
        logger.info("日志名称：" + name + ", 线程名称：" + Thread.currentThread().getName() + ", 正在运行。");
    }
}
