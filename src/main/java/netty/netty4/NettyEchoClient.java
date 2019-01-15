package netty.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import netty.NettyClient;

/**
 * @author jinzhimin
 * @description: Netty 4 版本的 EchoClient
 */
public class NettyEchoClient extends NettyClient{

    public NettyEchoClient(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        NettyEchoClient echoClient = new NettyEchoClient(new DelimiterClientChannelHandler());
        try {
            echoClient.connect(8080, "127.0.0.1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DelimiterClientChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());

        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new NettyEchoClientHandler());
    }
}