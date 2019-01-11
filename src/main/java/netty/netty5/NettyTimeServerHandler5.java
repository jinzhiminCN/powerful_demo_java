package netty.netty5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import nio.TimeOrderConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author jinzhimin
 * @description: Netty 5 版本的时间服务器处理类
 */
public class NettyTimeServerHandler5 extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyTimeServerHandler5.class);

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Netty Time Server Handler channelRead.");

        ByteBuf buf = (ByteBuf)msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String body = new String(req, "UTF-8");
        logger.info("The time server receive order:" + body);
        String currentTime = TimeOrderConst.QUERY_TIME_ORDER.equalsIgnoreCase(body) ?
                new Date(System.currentTimeMillis()).toString() : TimeOrderConst.BAD_ORDER;

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
