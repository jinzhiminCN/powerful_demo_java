package netty;

/**
 * @author jinzhimin
 * @description: ${description}
 */
public class NettyTimeClientTest {

    public void timeClientTest() throws Exception {
        new NettyTimeClient().connect(8080, "127.0.0.1");
    }

    public static void main(String[] args) {
        NettyTimeClientTest timeClientTest = new NettyTimeClientTest();
        try {
            timeClientTest.timeClientTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
