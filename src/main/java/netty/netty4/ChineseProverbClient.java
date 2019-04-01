package netty.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: 中文谚语客户端 UDP测试
 */
public class ChineseProverbClient {
    private static final Logger logger = LoggerFactory.getLogger(ChineseProverbClient.class);

    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChineseProverbClientHandler());

            ChannelFuture future = bootstrap.bind(0).sync();
            // 向网段内的所有机器广播UDP消息
            Channel channel = future.channel();
            channel.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("谚语字典查询？", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255", port))).sync();
            if(!channel.closeFuture().await(15000)){
                logger.info("查询超时！");
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        try {
            new ChineseProverbClient().run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ChineseProverbClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(ChineseProverbServerHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String resp = msg.content().toString(CharsetUtil.UTF_8);

        if (resp.startsWith("谚语查询结果：")) {
            logger.info(resp);
            ctx.close();
        }
    }
}

