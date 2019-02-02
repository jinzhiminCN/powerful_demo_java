package basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.annotation.meta.param;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author jinzhimin
 * @description: lambda 表达式 Demo
 * https://stackoverflow.com/questions/16635398/java-8-iterable-foreach-vs-foreach-loop/20177092#20177092
 */
public class LambdaDemo {
    private static final Logger logger = LoggerFactory.getLogger(LambdaDemo.class);

    private static int MaxSize = 10000000;

    private static List<Integer> integers = new ArrayList<>();

    static {
        for (int i = 0; i < MaxSize; i++) {
            int value = new Random().nextInt(MaxSize);
            integers.add(value);
        }
    }

    public static int iteratorMaxInteger() {
        int max = Integer.MIN_VALUE;
        for (Iterator<Integer> it = integers.iterator(); it.hasNext(); ) {
            max = Integer.max(max, it.next());
        }
        return max;
    }

    public static int forEachLoopMaxInteger() {
        int max = Integer.MIN_VALUE;
        for (Integer value : integers) {
            max = Integer.max(max, value);
        }
        return max;
    }

    public static int forMaxInteger() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < integers.size(); i++) {
            max = Integer.max(max, integers.get(i));
        }
        return max;
    }

    public static int parallelStreamMaxInteger() {
        Optional<Integer> max = integers.parallelStream().reduce(Integer::max);
        return max.get();
    }

    public static int lambdaMaxInteger() {
        return integers.stream().reduce(Integer.MIN_VALUE, (a, b) -> Integer.max(a, b));
    }

    public static int forEachLambdaMaxInteger() {
        final Wrapper wrapper = new Wrapper();
        wrapper.inner = Integer.MIN_VALUE;

        integers.forEach(i -> helper(i, wrapper));
        return wrapper.inner.intValue();
    }

    public static class Wrapper {
        public Integer inner;
    }

    private static int helper(int i, Wrapper wrapper) {
        wrapper.inner = Math.max(i, wrapper.inner);
        return wrapper.inner;
    }

    public static void compare() {
        // Iterator
        long begin = System.currentTimeMillis();
        iteratorMaxInteger();
        long end = System.currentTimeMillis();
        logger.info("iteratorMaxInteger: " + (end - begin));

        // For Each
        begin = System.currentTimeMillis();
        forEachLoopMaxInteger();
        end = System.currentTimeMillis();
        logger.info("forEachLoopMaxInteger: " + (end - begin));

        // For 循环
        begin = System.currentTimeMillis();
        forMaxInteger();
        end = System.currentTimeMillis();
        logger.info("forEachLoopMaxInteger: " + (end - begin));

        // parallelStream
        begin = System.currentTimeMillis();
        parallelStreamMaxInteger();
        end = System.currentTimeMillis();
        logger.info("parallelStreamMaxInteger: " + (end - begin));

        // lambda Max
        begin = System.currentTimeMillis();
        lambdaMaxInteger();
        end = System.currentTimeMillis();
        logger.info("lambdaMaxInteger: " + (end - begin));

        // forEach Lambda
        begin = System.currentTimeMillis();
        forEachLambdaMaxInteger();
        end = System.currentTimeMillis();
        logger.info("forEachLambdaMaxInteger: " + (end - begin));

    }

    public static void lambdaThread() {
        //Before Java 8:
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Before Java8 ");
            }
        }).start();

        // Java 8 way:
        new Thread(() -> System.out.println("In Java8!")).start();
    }

    public interface FunctionInterface0 {
        /**
         * 测试无参数，无返回值的方法
         */
        void test();
    }

    public interface FunctionInterface1 {
        /**
         * 测试有参数，无返回值的方法
         *
         * @param param
         */
        void test(int param);
    }

    public interface FunctionInterface2<T> {
        /**
         * 测试有参数，无返回值的模板方法
         *
         * @param param
         */
        void test(T param);
    }

    public interface FunctionInterface3<T> {
        /**
         * 测试有参数，有返回值的模板方法
         *
         * @param param
         * @return
         */
        boolean test(T param);
    }

    public static void useFunctionInterface0(FunctionInterface0 interface0) {
        interface0.test();
    }

    public static void useFunctionInterface1(FunctionInterface1 interface1) {
        int x = 1;
        interface1.test(x);
    }

    public static void useFunctionInterface2(FunctionInterface2<Integer> interface2) {
        int x = 1;
        interface2.test(x);
    }

    public static void useFunctionInterface3(FunctionInterface3<Integer> interface3) {
        int x = 1;
        boolean result = interface3.test(x);
        logger.info("useFunctionInterface3: " + result);
    }

    public static void testFunctionInterface() {
        // 使用Lambda表达式代替匿名内部类
        useFunctionInterface0(() -> System.out.println("Test FunctionInterface0"));

        useFunctionInterface1((x) -> System.out.println("Test FunctionInterface1, " + x));

        useFunctionInterface2((Integer x) -> System.out.println("Test FunctionInterface2, " + x));

        useFunctionInterface3((Integer x) -> {
            System.out.println("Test FunctionInterface3, " + x);
            return true;
        });
    }

    public static void lambdaList() {
        // Prior Java 8 :
        List<String> features = Arrays.asList("Lambdas", "Default Method",
                "Stream API", "Date and Time API");
        for (String feature : features) {
            System.out.println(feature);
        }

        // In Java 8:
        features = Arrays.asList("Lambdas", "Default Method", "Stream API",
                "Date and Time API");
        features.forEach(n -> System.out.println(n));

        // Even better use Method reference feature of Java 8
        // method reference is denoted by :: (double colon) operator
        // looks similar to score resolution operator of C++
        features.forEach(System.out::println);
    }

    public static void filter1(List<String> names, Predicate<String> condition) {
        for (String name : names) {
            if (condition.test(name)) {
                System.out.println(name + " ");
            }
        }
    }

    public static void filter2(List<String> names, Predicate<String> condition) {
        names.stream().filter((name) -> (condition.test(name)))
                .forEach((name) -> {
                    System.out.println(name + " ");
                });
    }

    public static void testFilter() {
        List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");

        System.out.println("Languages which starts with J :");
        filter1(languages, (str) -> str.startsWith("J"));

        System.out.println("Languages which ends with a ");
        filter1(languages, (str) -> str.endsWith("a"));

        System.out.println("Print all languages :");
        filter1(languages, (str) -> true);

        System.out.println("Print no language : ");
        filter2(languages, (str) -> false);

        System.out.println("Print language whose length greater than 4:");
        filter2(languages, (str) -> str.length() > 4);

        Predicate<String> startsWithJ = (n) -> n.startsWith("J");
        Predicate<String> fourLetterLong = (n) -> n.length() == 4;

        languages.stream()
                .filter(startsWithJ.and(fourLetterLong))
                .forEach((n) -> System.out.print("which starts with" +
                        " 'J' and four letter long is : " + n));
    }

    public static void main(String[] args) {
//        compare();
//        lambdaThread();
//        testFunctionInterface();
//        lambdaList();
        testFilter();
    }

}
