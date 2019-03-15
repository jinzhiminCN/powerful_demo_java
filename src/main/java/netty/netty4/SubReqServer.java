package netty.netty4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: 订购请求Server
 */
public class SubReqServer {
    private static final Logger logger = LoggerFactory.getLogger(SubReqServer.class);

    public void bind(int port) throws Exception {
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new SubReqServerHandler());
                        }
                    });

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

    public static void main(String[] args) {
        int port = 8080;

        SubReqServer subReqServer = new SubReqServer();
        try {
            subReqServer.bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SubReqServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;

        if ("zhangsan".equalsIgnoreCase(req.getUserName())) {
            logger.info("Service accept client subscribe req: [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }

        super.channelRead(ctx, msg);
    }

    private SubscribeResp resp(int subReqId){
         SubscribeResp resp = new SubscribeResp();
         resp.setSubReqID(subReqId);
         resp.setRespCode(0);
         resp.setDesc("Netty book order success, 3 days later, sent to the designated address");
         return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}