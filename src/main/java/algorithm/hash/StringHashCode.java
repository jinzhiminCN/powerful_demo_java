package algorithm.hash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: String的hashCode()方法运算结果查看
 * 重新计算Hash值的算法有很多，比如CRC32_HASH、FNV1_32_HASH、KETAMA_HASH等，
 * 其中KETAMA_HASH是默认的MemCache推荐的一致性Hash算法。
 * 参考 https://www.cnblogs.com/xrq730/p/5186728.html
 */
public class StringHashCode {
    private static final Logger logger = LoggerFactory.getLogger(StringHashCode.class);

    private static String[] servers = {
            "192.168.0.0:1111", "192.168.0.1:1111", "192.168.0.2:1111",
            "192.168.0.3:1111", "192.168.0.4:1111", "192.168.0.5:1111",
    };

    private static void stringHash(String value){
        logger.info(value + "的哈希值：" + value.hashCode());
    }

    private static void testStringHash(){
        for(String server:servers){
            stringHash(server);
        }
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     */
    public static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    public static void main(String[] args){
        testStringHash();
    }
}
