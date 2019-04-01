package netty.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author jinzhimin
 * @description: 文件服务器
 */
public class FileServer {
    private static final Logger logger = LoggerFactory.getLogger(FileServer.class);

    public void run(final int port) throws Exception {
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("StringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast("LineBasedFrameDecoder", new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast("StringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast("fileServerHandler", new FileServerHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("File服务器启动，端口：" + port);

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
        try {
            new ChineseProverbServer().run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class FileServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger = LoggerFactory.getLogger(ChineseProverbServerHandler.class);

    private static String CR = System.getProperty("line.separator");

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info(msg);

        File file = new File(msg);
        if (file.exists()) {
            if (file.isFile()) {
                ctx.writeAndFlush("Not a file:" + file + CR);
                return;
            }
            ctx.write(file + "" + file.length() + CR);

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
            ctx.write(region);
            ctx.writeAndFlush(CR);
        } else {
            ctx.writeAndFlush("File not found:" + file + CR);
        }
    }
}
