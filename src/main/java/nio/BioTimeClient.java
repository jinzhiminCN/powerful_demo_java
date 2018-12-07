package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @author jinzhimin
 * @description: ${description}
 */
public class BioTimeClient {
    private static final Logger logger = LoggerFactory.getLogger(BioTimeClient.class);

    public static final String QUERY_TIME_ORDER = "QUERY TIME ORDER";

    private int port = 8080;

    public void startClient() {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket("127.0.0.1", port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(QUERY_TIME_ORDER);
            logger.info("Send order 2 server succeed.");

            String response = in.readLine();
            logger.info("Now is: " + response);
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.info("关闭PrintWriter出现异常", e);
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.info("关闭BufferedReader出现异常", e);
            }

            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                logger.info("关闭Socket出现异常", e);
            }
        }

    }

    public static void main(String[] args) {
        BioTimeClient bioTimeClient = new BioTimeClient();
        bioTimeClient.startClient();
    }
}
