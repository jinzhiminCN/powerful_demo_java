package nio;

import algorithm.hash.ConsistentHashingWithoutVirtualNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author jinzhimin
 * @description: NIO server,
 *     reactor（反应器）模式，使用单线程模拟多线程，提高资源利用率和程序的效率，增加系统吞吐量。
 */
public class Reactor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Reactor.class);

    public int id = 100001;
    public int bufferSize = 2048;
    private int port = 4700;

    @Override
    public void run() {
        init();
    }

    public void init() {
        try {
            // 创建通道和选择器
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(
                    InetAddress.getLocalHost(), port);
            socketChannel.socket().bind(inetSocketAddress);

            // 设置通道非阻塞
            socketChannel.configureBlocking(false);
            // 绑定选择器
            socketChannel.register(selector, SelectionKey.OP_ACCEPT).attach(
                    id++);
            logger.info("Server started .... ip:" +  InetAddress.getLocalHost() + ", port:" + port);

            // 监听选择器
            listener(selector);
        } catch (Exception e) {
            logger.info("初始化服务器通道和选择器出现异常！", e);
        }
    }

    public void listener(Selector inSelector) {
        try {
            while (true) {
                Thread.sleep(1*1000);

                // 阻塞，直到有就绪事件为止
                inSelector.select();
                Set<SelectionKey> readySelectionKey = inSelector
                        .selectedKeys();
                Iterator<SelectionKey> it = readySelectionKey.iterator();
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    // 判断是哪个事件
                    // 客户请求连接
                    if (selectionKey.isAcceptable()) {
                        logger.info(selectionKey.attachment() + " - 接受请求事件");
                        // 获取通道 接受连接,
                        // 设置非阻塞模式（必须），同时需要注册 读写数据的事件，这样有消息触发时才能捕获
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey
                                .channel();
                        serverSocketChannel
                                .accept()
                                .configureBlocking(false)
                                .register(inSelector,
                                        SelectionKey.OP_READ | SelectionKey.OP_WRITE).attach(id++);
                        logger.info(selectionKey.attachment() + " - 已连接");
                    }

                    // 读数据
                    if (selectionKey.isReadable()) {
                        logger.info(selectionKey.attachment() + " - 读数据事件");
                        SocketChannel clientChannel = (SocketChannel)selectionKey.channel();
                        ByteBuffer receiveBuf = ByteBuffer.allocate(bufferSize);
                        clientChannel.read(receiveBuf);
                        logger.info(selectionKey.attachment()
                                + " - 读取数据：" + getString(receiveBuf));
                    }

                    // 写数据
                    if (selectionKey.isWritable()) {
                        logger.info(selectionKey.attachment() + " - 写数据事件");
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer sendBuf = ByteBuffer.allocate(bufferSize);
                        String sendText = "hello\n";
                        sendBuf.put(sendText.getBytes());
                        // 写完数据后调用此方法
                        sendBuf.flip();
                        clientChannel.write(sendBuf);
                    }

                    // 连接
                    if (selectionKey.isConnectable()) {
                        logger.info(selectionKey.attachment() + " - 连接事件");
                    }

                    // 必须removed 否则会继续存在，下一次循环还会进来,
                    // 注意removed 的位置，针对一个.next() remove一次
                    it.remove();
                }
            }
        } catch (Exception e) {
            logger.info("监听出错！", e);
        }

    }
    /**
     * ByteBuffer 转换 String
     * @param buffer
     * @return
     */
    public static String getString(ByteBuffer buffer) {
        StringBuilder strBuilder = new StringBuilder();
        try {
            for(int i = 0; i < buffer.position(); i++){
                strBuilder.append ((char)buffer.get(i));
            }
            return strBuilder.toString();
        } catch (Exception ex) {
            logger.info("从缓冲区ByteBuffer获取数据出错！", ex);
            return "";
        }
    }
}
