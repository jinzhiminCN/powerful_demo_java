package basic;

import basic.annotation.UseCaseAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jinzhimin
 * @description: UseCase注解
 */
public class UseCaseAnnotationDemo {
    private static final Logger logger = LoggerFactory.getLogger(UseCaseAnnotationDemo.class);

    @UseCaseAnnotation(id = 47, description = "Passwords must contain at least one numeric")
    public boolean validatePassword(String password) {
        return (password.matches("\\w*\\d\\w*"));
    }

    @UseCaseAnnotation(id = 48)
    public String encryptPassword(String password) {
        return new StringBuilder(password).reverse().toString();
    }

    public static void main(String[] args) {
        List<Integer> useCaseAnnotations = new ArrayList<>();
        Collections.addAll(useCaseAnnotations, 47, 48, 49, 50);
        trackUseCaseAnnotations(useCaseAnnotations, UseCaseAnnotationDemo.class);
    }

    public static void trackUseCaseAnnotations(List<Integer> useCaseAnnotations, Class<?> cl) {
        for (Method method : cl.getDeclaredMethods()) {
            UseCaseAnnotation uc = method.getAnnotation(UseCaseAnnotation.class);
            if (uc != null) {
                logger.info("Found Use Case:" + uc.id() + " "
                        + uc.description());
                useCaseAnnotations.remove(new Integer(uc.id()));
            }
        }
        for (int i : useCaseAnnotations) {
            logger.info("Warning: Missing use case-" + i);
        }
    }

}
