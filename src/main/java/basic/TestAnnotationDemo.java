package basic;

import basic.annotation.CheckAnnotation;
import basic.annotation.PerformAnnotation;
import basic.annotation.TestAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.annotation.meta.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author jinzhimin
 * @description: 运用TestAnnotation
 */

@TestAnnotation(msg="hello")
public class TestAnnotationDemo {
    private static final Logger logger = LoggerFactory.getLogger(TestAnnotationDemo.class);

    @CheckAnnotation(value="hi")
    int a;

    @PerformAnnotation
    public void testMethod(){}

    @SuppressWarnings("deprecation")
    public void test1(){
        Hero hero = new Hero();
        hero.say();
        hero.speak();
    }

    public static void main(String[] args) {
        boolean hasAnnotation = TestAnnotationDemo.class.isAnnotationPresent(TestAnnotation.class);

        if ( hasAnnotation ) {
            TestAnnotation testAnnotation = TestAnnotationDemo.class.getAnnotation(TestAnnotation.class);
            //获取类的注解
            logger.info("id:" + testAnnotation.id());
            logger.info("msg:" + testAnnotation.msg());
        }

        try {
            Field[] fields = TestAnnotationDemo.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                logger.info(field.getName());
            }

            Method[] methods = TestAnnotationDemo.class.getDeclaredMethods();
            for (int i = 0; i < fields.length; i++) {
                Method method = methods[i];
                logger.info(method.getName());
            }

            Field a = TestAnnotationDemo.class.getDeclaredField("a");
            a.setAccessible(true);
            // 获取一个成员变量上的注解
            CheckAnnotation check = a.getAnnotation(CheckAnnotation.class);

            if ( check != null ) {
                logger.info("check value:" + check.value());
            }

            Method testMethod = TestAnnotationDemo.class.getDeclaredMethod("testMethod");

            if ( testMethod != null ) {
                // 获取方法中的注解
                Annotation[] ans = testMethod.getAnnotations();
                for( int i = 0; i < ans.length; i++) {
                    logger.info("method testMethod annotation:" + ans[i].annotationType().getSimpleName());
                }
            }
        } catch (NoSuchFieldException e) {
            logger.info("类中不包含此属性！", e);
        } catch (SecurityException e) {
            logger.info("安全异常！", e);
        } catch (NoSuchMethodException e) {
            logger.info("类中不包含此方法！", e);
        }
    }
}


class Hero{
    private static final Logger logger = LoggerFactory.getLogger(Hero.class);

    @Deprecated
    public void say(){
        logger.info("Noting has to say!");
    }

    public void speak(){
        logger.info("I have a dream!");
    }
}