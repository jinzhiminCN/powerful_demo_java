package nio;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author jinzhimin
 * @description: 多路复用时间服务器
 */
public class MultiplexerTimeServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MultiplexerTimeServer.class);

    public static final String QUERY_TIME_ORDER = "QUERY TIME ORDER";
    public static final String BAD_ORDER = "BAD ORDER";

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            // 服务器通道绑定主机和端口
            InetSocketAddress inetSocketAddress = new InetSocketAddress(
                    InetAddress.getLocalHost(), port);
            serverSocketChannel.socket().bind(inetSocketAddress);
            // 设置通道非阻塞
            serverSocketChannel.configureBlocking(false);
            // 绑定选择器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Time Server started .... ip:" + InetAddress.getLocalHost() + ", port:" + port);

        } catch (IOException e) {
            logger.info("出现IO异常！", e);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // 可以是阻塞，非阻塞，也可以设置超时
                int readyChannels = selector.select(1000);

                if (readyChannels > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey readyKey = iterator.next();
                        handleInput(readyKey);
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                logger.info("出现IO异常！", e);
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                logger.info("关闭selector异常！", e);
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

                SocketChannel socketChannel = serverChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel socketChannel = (SocketChannel) key.channel();

                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);

                if(readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    logger.info("The time server receive order:" + body);

                    String currentTimeStr = new Date(System.currentTimeMillis()).toString();
                    String timeResult = QUERY_TIME_ORDER.equalsIgnoreCase(body) ? currentTimeStr : BAD_ORDER;

                    doWrite(socketChannel, timeResult);
                } else if (readBytes < 0){
                    // 对端链路关闭
                    key.cancel();
                    socketChannel.close();
                }

            }
        }

    }

    private void doWrite(SocketChannel socketChannel, String response) throws IOException {
         if(StringUtils.isNoneBlank(response)){
             byte[] bytes = response.getBytes();
             ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
             writeBuffer.put(bytes);
             writeBuffer.flip();
             socketChannel.write(writeBuffer);
         }
    }

}
