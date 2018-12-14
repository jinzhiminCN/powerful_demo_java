package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jinzhimin
 * @description: AIO时间服务器
 */
public class AcceptCompletionHandler implements
        CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(AcceptCompletionHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
        attachment.accept(attachment, this);

        ByteBuffer readBuf = ByteBuffer.allocate(1024);
        result.read(readBuf, readBuf, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
        attachment.accept(attachment, this);
    }
}
