package netty;

import netty.netty4.NettyTimeServer4;
import netty.netty5.NettyTimeServer5;

/**
 * @author jinzhimin
 * @description: Netty时间服务测试
 */
public class NettyTimeServerTest {

    public void timeServer5Test() throws Exception {
        new NettyTimeServer5().bind(8080);
    }

    public void timeServer4Test() throws Exception {
        new NettyTimeServer4().bind(8080);
    }

    public static void main(String[] args) {
        NettyTimeServerTest timeServerTest = new NettyTimeServerTest();
        try {
            timeServerTest.timeServer4Test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
