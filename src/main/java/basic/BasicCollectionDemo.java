package basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jinzhimin
 * @description: 基本集合类的测试
 * 参考: https://blog.csdn.net/luman1991/article/details/53034602
 */
public class BasicCollectionDemo {
    private static final Logger logger = LoggerFactory.getLogger(BasicCollectionDemo.class);

    public static void testInitHashMap() {
        // 使用普通方法初始化HashMap
        HashMap<String, String> mapInit1 = new HashMap<>(4);
        mapInit1.put("Name", "June");
        mapInit1.put("Age", "28");
        logger.info(GsonUtil.getGson().toJson(mapInit1));

        // 使用put方法初始化HashMap
        HashMap<String, String> mapInit2 = new HashMap<String, String>(4){
            {
                put("Name", "June");
                put("Age", "28");
            }
        };
        logger.info(GsonUtil.getGson().toJson(mapInit2));

        for(Map.Entry<String, String> entry : mapInit2.entrySet()){
            logger.info(entry.getKey() + ":" + entry.getValue());
        }

        logger.info(GsonUtil.getGson().toJson(mapInit2));
        logger.info(GsonUtil.getGson().toJson(new HashMap(mapInit2)));
    }

    public static void testInitList(){
        List<String> names = new ArrayList<String>() {
            {
                for (int i = 0; i < 10; i++) {
                    add("A" + i);
                }
            }
        };
        logger.info(names.toString());
    }

    public static void testInstanceDemo(){
        new InstanceDemo();
        new InstanceDemo();
    }

    public static void main(String[] args) {
        testInitHashMap();
//        testInstanceDemo();
//          testInitList();
    }

}

class InstanceDemo {
    private static final Logger logger = LoggerFactory.getLogger(InstanceDemo.class);

    public InstanceDemo(){
        logger.info("Constructor called：构造器被调用");
    }

    static {
        logger.info("Static block called：静态块被调用");
    }

    /**
     * 第一层括弧实际是定义了一个匿名内部类 (Anonymous Inner Class)，
     * 第二层括弧实际上是一个实例初始化块 (instance initializer block)，这个块在内部匿名类构造时被执行。
     * 之所以被叫做“实例初始化块”是因为它们被定义在了一个类的实例范围内。
     */
    {
        logger.info("Instance initializer called：实例初始化块被调用");
    }

}