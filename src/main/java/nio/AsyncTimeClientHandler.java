package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author jinzhimin
 * @description: ${description}
 */
public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTimeClientHandler.class);

    private AsynchronousSocketChannel asynchronousSocketChannel;
    private String host;
    private int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            asynchronousSocketChannel = AsynchronousSocketChannel.open();
            logger.info("Time Client started");
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);

        try {
            asynchronousSocketChannel.connect(new InetSocketAddress(host, port), this, this);
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.info("出现中断异常", e);
            }
        } finally {
            try {
                asynchronousSocketChannel.close();
            } catch (IOException e) {
                logger.info("关闭出现IO异常", e);
            }
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = TimeOrderConst.QUERY_TIME_ORDER.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        asynchronousSocketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    asynchronousSocketChannel.write(buffer, buffer, this);
                } else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    asynchronousSocketChannel.read(readBuffer, readBuffer,
                            new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer result, ByteBuffer buffer) {
                                    buffer.flip();
                                    byte[] bytes = new byte[buffer.remaining()];
                                    buffer.get(bytes);
                                    String body;
                                    try {
                                        body = new String(bytes, "UTF-8");
                                        logger.info("Current Time:" + body);
                                    } catch (UnsupportedEncodingException e) {
                                        logger.info("关闭不支持的编码异常", e);
                                    } finally {
                                        latch.countDown();
                                    }
                                }

                                @Override
                                public void failed(Throwable exc, ByteBuffer buffer) {
                                    try {
                                        asynchronousSocketChannel.close();
                                    } catch (IOException e) {
                                        logger.info("关闭出现IO异常", e);
                                    } finally {
                                        latch.countDown();
                                    }
                                }
                            });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                try {
                    asynchronousSocketChannel.close();
                } catch (IOException e) {
                    logger.info("关闭出现IO异常", e);
                } finally {
                    latch.countDown();
                }
            }
        });

    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        logger.info("调用失败！", exc);
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            logger.info("关闭出现IO异常", e);
        } finally {
            latch.countDown();
        }
    }
}
