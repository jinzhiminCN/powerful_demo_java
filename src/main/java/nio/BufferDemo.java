package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.*;

/**
 * @author jinzhimin
 * @description: NIO buffer示例。
 */
public class BufferDemo {
    private static final Logger logger = LoggerFactory.getLogger(Reactor.class);

    public static void printBuffer(ByteBuffer byteBuf){
        while(byteBuf.hasRemaining()){
            char value = (char) byteBuf.get();
            char[] valueArray = new char[]{value};
            logger.info(new String(valueArray));
        }
    }

    public static void testByteBuffer(){
        ByteBuffer byteBuf = ByteBuffer.allocate(100);
        byteBuf.put("123".getBytes());
        byteBuf.put("abc".getBytes());
        byteBuf.flip();

        printBuffer(byteBuf);

        byteBuf.rewind();
        printBuffer(byteBuf);

        byteBuf.clear();
        byteBuf.put("456".getBytes());
        byteBuf.flip();
        printBuffer(byteBuf);
    }

    public static void main(String[] args) {
        testByteBuffer();
    }
}
