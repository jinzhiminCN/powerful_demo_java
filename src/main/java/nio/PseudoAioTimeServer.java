package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jinzhimin
 * @description: 伪异步IO时间服务器
 */
public class PseudoAioTimeServer {
    private static final Logger logger = LoggerFactory.getLogger(PseudoAioTimeServer.class);

    private int port = 8080;

    public void startServer() {
        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            logger.info("The time server is start in port:" + port);

            Socket socket = null;
            TimeServerHandlerExecutePool serverHandlerExecutePool = new TimeServerHandlerExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                serverHandlerExecutePool.execute(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        } finally {
            try {
                if (server != null) {
                    logger.info("The time server close");
                    server.close();
                }
            } catch (IOException e) {
                logger.info("关闭ServerSocket出现异常", e);
            }
        }
    }

    public static void main(String[] args) {
        PseudoAioTimeServer aioTimeServer = new PseudoAioTimeServer();
        aioTimeServer.startServer();
    }

}
