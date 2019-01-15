package netty.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import netty.NettyClient;
import netty.netty5.NettyTimeClientHandler5;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的 TimeClient
 */
public class NettyTimeClient4 extends NettyClient{

    public NettyTimeClient4(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        NettyTimeClient4 timeClient = new NettyTimeClient4(new LineClientChannelHandler());
        try {
            timeClient.connect(8080, "127.0.0.1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class LineClientChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new NettyTimeClientHandler4());
    }
}
