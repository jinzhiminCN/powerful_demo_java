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
 * @description: ${description}
 */
public class NettyTimeClientHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyTimeClientHandler.class);

    private final ByteBuf firstMessage;

    public NettyTimeClientHandler() {
        byte[] req = TimeOrderConst.QUERY_TIME_ORDER.getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(firstMessage);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
