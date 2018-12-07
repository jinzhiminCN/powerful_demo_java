package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author jinzhimin
 * @description: NIO channel示例。
 */
public class ChannelDemo {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDemo.class);

    private static final String fromFilePath = "fromFile.txt";
    private static final String toFilePath = "toFile.txt";

    public static void testFileChannel(){
        RandomAccessFile raFile = null;
        try {
            raFile = new RandomAccessFile(fromFilePath, "rw");
            FileChannel inChannel = raFile.getChannel();

            ByteBuffer buf = ByteBuffer.allocate(48);

            int bytesRead = inChannel.read(buf);
            while (bytesRead != -1) {
                logger.info("Read " + bytesRead);

                // 写模式转换为读模式
                buf.flip();

                while(buf.hasRemaining()){
                    char value = (char) buf.get();
                    char[] valueArray = new char[]{value};
                    logger.info(new String(valueArray));
                }

                buf.clear();
                bytesRead = inChannel.read(buf);
            }
        } catch (IOException e) {
            logger.info("IO异常！", e);
        } finally {
            try {
                if(raFile != null) {
                    raFile.close();
                }
            } catch (IOException e) {
                logger.info("IO关闭异常！", e);
            }
        }
    }

    public static void testTransferChannel(){
        try {
            RandomAccessFile fromFile = new RandomAccessFile(fromFilePath, "rw");
            FileChannel      fromChannel = fromFile.getChannel();

            RandomAccessFile toFile = new RandomAccessFile(toFilePath, "rw");
            FileChannel      toChannel = toFile.getChannel();

            long position = 0;
            long count = fromChannel.size();

            toChannel.transferFrom(fromChannel, position, count);
        } catch (IOException e) {
            logger.info("出现IO异常！", e);
        }
    }

    /**
     * 目前不能运行。
     */
    public static void testSocketChannel(){
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("www.baidu.com", 80));

            ByteBuffer buf = ByteBuffer.allocate(102400);
            int bytesRead = socketChannel.read(buf);

            while (bytesRead != -1) {
                logger.info("Read " + bytesRead);

                // 写模式转换为读模式
                buf.flip();

                byte[] valueArray = new byte[48];
                while(buf.hasRemaining()){
                    buf.get(valueArray);
                    logger.info(new String(valueArray));
                }

                buf.clear();
                bytesRead = socketChannel.read(buf);
            }

        } catch (IOException e) {
            logger.info("出现IO异常！", e);
        } finally {
            try {
                if(socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException e) {
                logger.info("IO关闭异常！", e);
            }
        }

    }


    public static void main(String[] args) {
//        testFileChannel();
//        testTransferChannel();
        testSocketChannel();
    }
}
