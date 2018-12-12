package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jinzhimin
 * @description: NIO时间服务器
 */
public class NioTimeServer {
    private static final Logger logger = LoggerFactory.getLogger(NioServer.class);

    private int port = 8080;

    public void startServer() {
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);

        new Thread(timeServer, "NOI-MultiplexerTimeServer-001").start();
    }

    public static void main(String[] args) {
        NioTimeServer nioTimeServer = new NioTimeServer();
        nioTimeServer.startServer();
    }
}
