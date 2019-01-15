package netty.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nio.TimeOrderConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的 EchoClientHandler
 */
public class NettyEchoClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyEchoClientHandler.class);

    private int counter;

    private static final String ECHO_REQ = "Hi, Welcome to Netty.$_";

    public NettyEchoClientHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Netty Time Client Handler channelActive.");

        ByteBuf message = null;
        for(int i = 0; i < 10; i++){
             message = Unpooled.copiedBuffer(ECHO_REQ.getBytes());
             ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Netty Time Client Handler channelRead.");
        counter++;

        String body = (String)msg;
        logger.info("This is:" + counter + " times receive server: [" + body +"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexcepted exception from downstream:" + cause.getMessage());
        ctx.close();
    }

}
