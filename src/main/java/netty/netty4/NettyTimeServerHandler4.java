package netty.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nio.TimeOrderConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的时间服务器处理类
 */
public class NettyTimeServerHandler4 extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyTimeServerHandler4.class);

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Netty Time Server Handler channelRead.");
        counter++;

        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String body = new String(req, "UTF-8")
                .substring(0, req.length - System.getProperty("line.separator").length());
        logger.info("The time server receive order:" + body + "; the counter is :" + counter);
        String currentTime = TimeOrderConst.QUERY_TIME_ORDER.equalsIgnoreCase(body) ?
                new Date(System.currentTimeMillis()).toString() : TimeOrderConst.BAD_ORDER;

        // 增加换行符
        currentTime = currentTime + System.getProperty("line.separator");

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
