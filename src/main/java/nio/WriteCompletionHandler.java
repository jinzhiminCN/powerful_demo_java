package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * @author jinzhimin
 * @description: AIO时间服务器
 */
public class WriteCompletionHandler implements
        CompletionHandler<Integer, ByteBuffer> {
    private static final Logger logger = LoggerFactory.getLogger(WriteCompletionHandler.class);

    private AsynchronousSocketChannel asynchronousSocketChannel;

    private ReadCompletionHandler readCompletionHandler;

    public WriteCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel, ReadCompletionHandler readCompletionHandler) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.readCompletionHandler = readCompletionHandler;
    }

    @Override
    public void completed(Integer result, ByteBuffer repBuf) {
        if (repBuf.hasRemaining()) {
            asynchronousSocketChannel.write(repBuf, repBuf, this);
        }
        // 写完成后(对端读取完成)，再尝试读（半双工模式）
        else {
            // 继续尝试读取对端发送的数据
            ByteBuffer readBuf = ByteBuffer.allocate(1024);
            asynchronousSocketChannel.read(readBuf, readBuf, readCompletionHandler);
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer repBuf) {
        exc.printStackTrace();
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        }
    }

}
