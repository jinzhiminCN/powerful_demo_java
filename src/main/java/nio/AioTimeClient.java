package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: AIO时间服务器
 */
public class AioTimeClient {
    private static final Logger logger = LoggerFactory.getLogger(AioTimeClient.class);

    private int port = 8080;

    public void startClient() {
        AsyncTimeClientHandler timeClient = new AsyncTimeClientHandler("127.0.0.1", port);

        new Thread(timeClient, "AIO-AsyncTimeClientHandler-001").start();
    }

    public static void main(String[] args) {
        AioTimeClient aioTimeClient = new AioTimeClient();
        aioTimeClient.startClient();
    }
}
