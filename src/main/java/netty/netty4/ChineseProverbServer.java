package netty.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author jinzhimin
 * @description: 中文谚语服务器 UDP测试
 */
public class ChineseProverbServer {
    private static final Logger logger = LoggerFactory.getLogger(ChineseProverbServer.class);

    public void run(final int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChineseProverbServerHandler());

            // 绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("ChineseProverbServer，端口：" + port);

            // 等待服务端监听端口关闭
            future.channel().closeFuture().await();
        } finally {
            // 优雅退出，释放线程池资源
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        try {
            new ChineseProverbServer().run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(ChineseProverbServerHandler.class);

    private static final String[] DICTIONARY = {
            "只有功夫深，铁杵磨成针。",
            "旧时王谢堂前燕，飞入寻常百姓家。",
            "洛阳亲友如相问，一片冰心在玉壶。",
            "老骥伏枥，志在千里。",
            "一寸光阴一寸金，寸金难买寸光阴"
    };

    private String nextQuote() {
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String req = msg.content().toString(CharsetUtil.UTF_8);
        logger.info(req);

        if("谚语字典查询？".equals(req)){
            ByteBuf byteBuf = Unpooled.copiedBuffer(
                    "谚语查询结果：" + nextQuote(), CharsetUtil.UTF_8);
            ctx.writeAndFlush(new DatagramPacket(byteBuf, msg.sender()));
        }
    }
}
