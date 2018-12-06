package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

/**
 * @author jinzhimin
 * @description: NIO channel示例。
 * @email jinzhimin@youxin.com
 * @since 2018/12/6 14:32
 */
public class ChannelDemo {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDemo.class);

    public static void testFileChannel(){
        try {
            RandomAccessFile raFile = new RandomAccessFile("test.txt", "rw");
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
            raFile.close();
        } catch (IOException e) {
            logger.info("IO异常！", e);
        }
    }



    public static void main(String[] args) {
        testFileChannel();
    }
}
