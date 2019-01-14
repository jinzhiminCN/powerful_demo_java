package netty;

import netty.netty4.NettyTimeClient4;
import netty.netty5.NettyTimeClient5;

/**
 * @author jinzhimin
 * @description: 测试 NettyTimeClient
 */
public class NettyTimeClientTest {

    public void timeClient5Test() throws Exception {
        new NettyTimeClient5().connect(8080, "127.0.0.1");
    }

    public void timeClient4Test() throws Exception {
        new NettyTimeClient4().connect(8080, "127.0.0.1");
    }

    public static void main(String[] args) {
        NettyTimeClientTest timeClientTest = new NettyTimeClientTest();
        try {
            timeClientTest.timeClient4Test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
