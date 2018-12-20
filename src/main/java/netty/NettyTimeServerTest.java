package netty;

/**
 * @author jinzhimin
 * @description: Netty时间服务测试
 */
public class NettyTimeServerTest {

    public void timeServerTest() throws Exception {
        new NettyTimeServer().bind(8080);
    }

    public static void main(String[] args) {
        NettyTimeServerTest timeServerTest = new NettyTimeServerTest();
        try {
            timeServerTest.timeServerTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
