package algorithm.hash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author jinzhimin
 * @description: 不带虚拟节点的一致性Hash算法。
 */
public class ConsistentHashingWithoutVirtualNode {
    private static final Logger logger = LoggerFactory.getLogger(ConsistentHashingWithoutVirtualNode.class);

    /**
     * 待添加入Hash环的服务器列表
     */
    private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
            "192.168.0.3:111", "192.168.0.4:111"};

    /**
     * key表示服务器的hash值，value表示服务器的名称
     */
    private static SortedMap<Integer, String> sortedMap =
            new TreeMap<>();

    /**
     * 程序初始化，将所有的服务器放入sortedMap中
     */
    static {
        for (int i = 0; i < servers.length; i++) {
            int hash = StringHashCode.getHash(servers[i]);
            logger.info("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
            sortedMap.put(hash, servers[i]);
        }
    }

    /**
     * 得到应当路由到的结点
     */
    private static String getServer(String node) {
        // 得到带路由的结点的Hash值
        int hash = StringHashCode.getHash(node);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, String> subMap =
                sortedMap.tailMap(hash);
        // 第一个Key就是顺时针过去离node最近的那个结点
        Integer serverCode = subMap.firstKey();
        // 返回对应的服务器名称
        return subMap.get(serverCode);
    }

    public static void main(String[] args) {
        String[] nodes = {"127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333"};
        for (int i = 0; i < nodes.length; i++) {
            logger.info("[" + nodes[i] + "]的hash值为" +
                    StringHashCode.getHash(nodes[i]) + ", 被路由到结点[" + getServer(nodes[i]) + "]");
        }
    }
}
