package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author jinzhimin
 * @description: NIO server
 */
public class NioServer {
    private static final Logger logger = LoggerFactory.getLogger(NioServer.class);

    public static void startNioServer() throws Exception {
        char name = 'A';

        // 创建通道,并设置非阻塞
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        // 创建选择器，并为通道绑定感兴趣的事件
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT).attach("主监听通道");
        logger.info("监听的channel Id：" + serverSocketChannel.hashCode());

        // 通道绑定端口号
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 4700);
        serverSocketChannel.socket().bind(inetSocketAddress);

        // 开始轮询通道事件
        while (true) {
            // 可以是阻塞，非阻塞，也可以设置超时
            int readyChannels = selector.selectNow();

            if (readyChannels > 0) {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey readyKey = (SelectionKey) iterator.next();
                    iterator.remove();

                    if (readyKey.isAcceptable()) {
                        ServerSocketChannel readyChannel = (ServerSocketChannel) readyKey.channel();
                        logger.info("可接受连接的channel Id:" + readyChannel.hashCode() + readyKey.attachment());

                        SocketChannel socketChannel1 = (SocketChannel) readyChannel.accept().configureBlocking(false);
                        logger.info("接受连接后返回的channel Id" + socketChannel1.hashCode());

                        socketChannel1.register(selector, (SelectionKey.OP_READ | SelectionKey.OP_WRITE)).attach(name);
                        name++;
                    }

                    if (readyKey.isWritable()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SocketChannel readyChannel = (SocketChannel) readyKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(512);
                        buffer.put(("to " + readyKey.attachment() + " " + dateFormat.format(new Date()) + "\n").getBytes());
                        buffer.flip();
                        readyChannel.write(buffer);
                    }

                    if (readyKey.isReadable()) {
                        SocketChannel readyChannel = (SocketChannel) readyKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(512);
                        readyChannel.read(buffer);
                        logger.info(readyKey.attachment() + " " + getString(buffer));
                    }
                }
            }
            Thread.sleep(1000);
        }
    }

    /**
     * ByteBuffer 转换 String
     *
     * @param buffer
     * @return
     */
    public static String getString(ByteBuffer buffer) {
        String string = "";
        try {
            for (int i = 0; i < buffer.position(); i++) {
                string += (char) buffer.get(i);
            }
            return string;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {

    }

}
