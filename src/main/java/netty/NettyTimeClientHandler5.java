package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import nio.TimeOrderConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: Netty 5 版本的 TimeClientHandler
 */
public class NettyTimeClientHandler5 extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyTimeClientHandler5.class);

    private final ByteBuf firstMessage;

    public NettyTimeClientHandler5() {
        logger.info("Netty Time Client Handler Init.");
        byte[] req = TimeOrderConst.QUERY_TIME_ORDER.getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Netty Time Client Handler channelActive.");
        ctx.writeAndFlush(firstMessage);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Netty Time Client Handler channelRead.");
        ByteBuf buf = (ByteBuf)msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String body = new String(req, "UTF-8");
        logger.info("Now is:" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexcepted exception from downstream:" + cause.getMessage());
        ctx.close();
    }

}
