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
public class TimeServerHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TimeServerHandler.class);

    public static final String QUERY_TIME_ORDER = "QUERY TIME ORDER";
    public static final String BAD_ORDER = "BAD ORDER";

    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);

            String timeResult = null;
            String currentTimeStr = null;
            String body = null;

            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }

                logger.info("The time server receive order: " + body);

                currentTimeStr = new Date(System.currentTimeMillis()).toString();
                timeResult = QUERY_TIME_ORDER.equalsIgnoreCase(body) ? currentTimeStr : BAD_ORDER;
                out.println(timeResult);
            }
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.info("关闭BufferedReader出现异常", e);
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.info("关闭PrintWriter出现异常", e);
            }

            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            } catch (Exception e) {
                logger.info("关闭Socket出现异常", e);
            }
        }
    }

}
