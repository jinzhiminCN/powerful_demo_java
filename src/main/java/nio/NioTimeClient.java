package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: NIO时间客户端
 */
public class NioTimeClient {
    private static final Logger logger = LoggerFactory.getLogger(NioTimeClient.class);

    private int port = 8080;

    public void startClient() {
        TimeClientHandler timeClient = new TimeClientHandler("10.255.6.144", port);

        new Thread(timeClient, "TimeClient-001").start();
    }

    public static void main(String[] args) {
        NioTimeClient nioTimeClient = new NioTimeClient();
        nioTimeClient.startClient();
    }
}
