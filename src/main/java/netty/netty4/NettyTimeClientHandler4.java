package netty.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nio.TimeOrderConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的 TimeClientHandler
 */
public class NettyTimeClientHandler4 extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyTimeClientHandler4.class);

    private int counter;

    private final ByteBuf firstMessage;

    private byte[] reqBytes;

    public NettyTimeClientHandler4() {
        logger.info("Netty Time Client Handler Init.");
        byte[] req = TimeOrderConst.QUERY_TIME_ORDER.getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);

        reqBytes = TimeOrderConst.QUERY_TIME_ORDER.getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Netty Time Client Handler channelActive.");
//        ctx.writeAndFlush(firstMessage);

        ByteBuf message = null;
        for(int i = 0; i < 100; i++){
             message = Unpooled.buffer(reqBytes.length);
             message.writeBytes(reqBytes);
             ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Netty Time Client Handler channelRead.");
        counter++;

        ByteBuf buf = (ByteBuf)msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String body = new String(req, "UTF-8");
        logger.info("Now is:" + body + "; the counter is: " + counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexcepted exception from downstream:" + cause.getMessage());
        ctx.close();
    }

}
