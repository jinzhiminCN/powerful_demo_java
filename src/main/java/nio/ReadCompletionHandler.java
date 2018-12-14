package nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * @author jinzhimin
 * @description: AIO时间服务器
 */
public class ReadCompletionHandler implements
        CompletionHandler<Integer, ByteBuffer> {
    private static final Logger logger = LoggerFactory.getLogger(ReadCompletionHandler.class);

    private AsynchronousSocketChannel asynchronousSocketChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void completed(Integer result, ByteBuffer readedData) {
        // 如果对端链路关闭
        if (result < 0) {
            try {
                asynchronousSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 如果读取到对端发送过来的数据
        if (result > 0) {
            readedData.flip();
            byte[] data = new byte[readedData.remaining()];
            readedData.get(data);
            String command = null;
            try {
                command = new String(data, "UTF-8");
                if ("time\r\n".equalsIgnoreCase(command)) {
                    doWrite(new Date().toString() + "\r\n");
                } else if ("stop\r\n".equalsIgnoreCase(command)) {
                    doWriteAndClose("bye.\r\n");
                } else if ("\r\n".equalsIgnoreCase(command)) {
                    doWrite("\r\n");
                } else {
                    doWrite("unknown command\r\n");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                doWrite("server error\r\n");
            }
        }
        //如果未读取到数据
        else {
            //继续尝试读取对端发送的数据
            ByteBuffer readBuf = ByteBuffer.allocate(1024);
            asynchronousSocketChannel.read(readBuf, readBuf, this);
        }
    }

    private void doWriteAndClose(String response) {
        ByteBuffer repBuf = null;
        try {
            repBuf = ByteBuffer.wrap(response.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (repBuf != null) {
            asynchronousSocketChannel.write(repBuf, repBuf, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer repBuf) {
                    if (repBuf.hasRemaining()) {
                        asynchronousSocketChannel.write(repBuf, repBuf, this);
                    }
                    //写完成后，关闭链路
                    else {
                        try {
                            asynchronousSocketChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer repBuf) {
                    exc.printStackTrace();
                    try {
                        asynchronousSocketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void doWrite(String response) {
        ByteBuffer repBuf = null;
        try {
            repBuf = ByteBuffer.wrap(response.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (repBuf != null) {
            asynchronousSocketChannel.write(repBuf, repBuf, new WriteCompletionHandler(asynchronousSocketChannel, this));
        }
    }


    @Override
    public void failed(Throwable exc, ByteBuffer readedData) {
        exc.printStackTrace();
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
