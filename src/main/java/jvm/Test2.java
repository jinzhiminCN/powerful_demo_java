package jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: 测试类
 */
public class Test2 {
    private static final Logger logger = LoggerFactory.getLogger(Test2.class);

    static {
        logger.info("Test2 静态初始化块执行了！");
    }
}
