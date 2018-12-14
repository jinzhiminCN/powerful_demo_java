package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author jinzhimin
 * @description: ${description}
 */
public class AsyncTimeServerHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTimeServerHandler.class);

    public static final String QUERY_TIME_ORDER = "QUERY TIME ORDER";
    public static final String BAD_ORDER = "BAD ORDER";

    private int port;

    private CountDownLatch latch;

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));

            logger.info("Time Server started .... ip:" + InetAddress.getLocalHost() + ", port:" + port);
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);

        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.info("出现中断异常", e);
        }
    }

    public void doAccept() {
        asynchronousServerSocketChannel.accept(asynchronousServerSocketChannel, new AcceptCompletionHandler());
    }

}
