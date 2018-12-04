package basic;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author jinzhimin
 * @description: Java8的新日期和时间测试，包括Instant, LocalDateTime，LocalDate，LocalTime等。
 */
public class NewDateTimeDemo {
    private static final Logger logger = LoggerFactory.getLogger(NewDateTimeDemo.class);

    public static void testPrintInstant() {
        // 获得当前时间
        Instant instant = Instant.now();
        System.out.println(instant);
    }

    public static void testDateTransform() {
        Date date = new Date();
        long timeStamp = date.getTime();
        long timeMillis = System.currentTimeMillis();

        // 将java.util.Date转换为Instant
        Instant instant = Instant.ofEpochMilli(timeStamp);
        logger.info(timeStamp + ", " + timeMillis + ", Instant: " + instant);

        // 从字符串类型中创建Instant类型的时间
        instant = Instant.parse("1995-10-23T10:12:35Z");
        logger.info("Instant: " + instant);

        Instant instant1 = instant.plus(Duration.ofHours(5).plusMinutes(4));
        logger.info("Instant: " + instant1);

        // 计算5天前的时间
        Instant instant2 = instant.minus(5, ChronoUnit.DAYS);
        Instant instant3 = instant.minus(Duration.ofDays(5));
        logger.info("Instant2: " + instant2 + ", Instant3: " + instant3);

        // 计算两个Instant之间的分钟数
        long diffAsMinutes1 = instant.until(instant1, ChronoUnit.MINUTES);
        long diffAsMinutes2 = ChronoUnit.MINUTES.between(instant, instant1);
        logger.info("diffAsMinutes1: " + diffAsMinutes1 + ", diffAsMinutes2: " + diffAsMinutes2);

        // 用compareTo方法比较
        logger.info("compare instant: " + instant1.compareTo(instant));
        // 使用isAfter()和isBefore()
        logger.info("instant1.isAfter(instant)={}, instant1.isBefore(instant)={}",
                instant1.isAfter(instant), instant1.isBefore(instant));
    }

    public static void testLocalDateTime() {
        // 获取秒数
        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        // 获取毫秒数
        Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        logger.info("Second:" + second + ", milliSecond:" + milliSecond);

        // 时间转字符串格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTimeStr = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        logger.info("DateTime:" + dateTimeStr);

        // 字符串转时间
        String dateTimeStr2 = "2018-08-21 12:11:49";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr2, df);
        logger.info("LocalDateTime:" + dateTime);

    }

    /**
     * 将java.util.Date 转换为java8 的java.time.LocalDateTime,默认时区为东8区
     * @param date
     * @return
     */
    public static LocalDateTime dateConvertToLocalDateTime(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
    }

    /**
     * 将java8 的 java.time.LocalDateTime 转换为 java.util.Date，默认时区为东8区
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }


    public static void main(String[] args) {
        testDateTransform();
    }
}
