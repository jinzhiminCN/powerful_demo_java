package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import netty.netty4.NettyEchoServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FtpClientUtil;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的Echo服务器
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    protected ChannelInitializer<SocketChannel> channelInitializer;

    public NettyServer(){
    }

    public NettyServer(ChannelInitializer<SocketChannel> channelInitializer){
        this.channelInitializer = channelInitializer;
    }

    public void bind(int port) throws Exception {
        // NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(channelInitializer);

            // 绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

