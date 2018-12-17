package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: AIO时间服务器
 */
public class AioTimeServer {
    private static final Logger logger = LoggerFactory.getLogger(AioTimeServer.class);

    private int port = 8080;

    public void startServer() {
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);

        new Thread(timeServer, "NOI-MultiplexerTimeServer-001").start();
    }

    public static void main(String[] args) {
        AioTimeServer aioTimeServer = new AioTimeServer();
        aioTimeServer.startServer();
    }
}
