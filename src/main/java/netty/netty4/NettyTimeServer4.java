package netty.netty4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import netty.NettyServer;
import netty.netty5.NettyTimeServerHandler5;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的时间服务器
 */
public class NettyTimeServer4 extends NettyServer {

    public NettyTimeServer4(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        NettyTimeServer4 timeServer = new NettyTimeServer4(new LineServerChannelHandler());
        try {
            timeServer.bind(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class LineServerChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new NettyTimeServerHandler4());
    }
}