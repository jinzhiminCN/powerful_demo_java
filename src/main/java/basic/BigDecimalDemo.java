package basic;

import encrypt.HmacDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author jinzhimin
 * @description: BigDecimal测试。
 */
public class BigDecimalDemo {
    private static final Logger logger = LoggerFactory.getLogger(BigDecimalDemo.class);

    /**
     * 测试BigDecimal定义。
     */
    public static void testBigDecimalDefine(){
        BigDecimal big1 = new BigDecimal(0.333);
        BigDecimal big2 = new BigDecimal("0.333");

        logger.info(big1 + "");
        logger.info(big2 + "");
    }

    /**
     * 测试BigDecimal比较。
     */
    public static void testBigDecimalCompare(){
        BigDecimal big3 = new BigDecimal(1);
        BigDecimal big4 = new BigDecimal("1.000");
        logger.info(big3.compareTo(big4) + "");
        logger.info(big3.equals(big4) + "");
    }

    public static void main(String[] args) {
//        testBigDecimalDefine();
        testBigDecimalCompare();
    }
}
