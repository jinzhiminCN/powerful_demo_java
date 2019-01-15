package netty.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nio.TimeOrderConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的Echo服务器处理类
 */
public class NettyEchoServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyEchoServerHandler.class);

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Netty Time Server Handler channelRead.");
        counter++;

        String body = (String) msg;
        logger.info("This is " + counter + " times receiver client: [" + body + "]");
        body += "$_";

        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.write(echo);
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
