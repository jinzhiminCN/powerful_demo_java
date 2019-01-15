package netty.netty4;

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
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import netty.NettyServer;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的Echo服务器
 */
public class NettyEchoServer extends NettyServer{

    public NettyEchoServer(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        NettyEchoServer echoServer = new NettyEchoServer(new FixedLengthServerChannelHandler());
        try {
            echoServer.bind(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DelimiterServerChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());

        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new NettyEchoServerHandler());
    }
}

class FixedLengthServerChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new NettyEchoServerHandler());
    }
}