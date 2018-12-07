package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jinzhimin
 * @description: BIO服务
 */
public class BioServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Reactor.class);

    @Override
    public void run() {
        logger.info("Hello Server!!");

        ServerSocket server = null;
        Socket socket = null;
        BufferedReader bufReader = null;

        try {
            try {
                // 创建一个ServerSocket在端口4700监听客户请求
                server = new ServerSocket(4700);
            } catch (Exception e) {
                logger.info("can not listen to server:" + e);
            }

            try {
                // 使用accept()阻塞等待客户请求，有客户请求到来则产生一个Socket对象，并继续执行
                socket = server.accept();
            } catch (Exception e) {
                logger.info("accept Error." + e);
            }

            String line;
            // 由Socket对象得到输入流，并构造相应的BufferedReader对象
            bufReader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            logger.info("Client:" + bufReader.readLine());

            PrintWriter os = new PrintWriter(socket.getOutputStream());
            line = "hello";
            // 向客户端输出该字符串
            os.println(line);
            os.flush();
        } catch (Exception e) {
            logger.info("Error." + e);
        } finally {
            try {
                if (bufReader != null) {
                    // 关闭Socket输入流
                    bufReader.close();
                }

                if (socket != null) {
                    // 关闭Socket
                    socket.close();
                }

                if (server != null) {
                    // 关闭ServerSocket
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
