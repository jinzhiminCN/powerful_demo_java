package netty.netty5;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import netty.NettyClient;
import netty.netty4.NettyTimeClient4;

/**
 * @author jinzhimin
 * @description: Netty 5 版本的 TimeClient
 */
public class NettyTimeClient5 extends NettyClient{

    public NettyTimeClient5(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        NettyTimeClient4 timeClient = new NettyTimeClient4(new ChildClientChannelHandler());
        try {
            timeClient.connect(8080, "127.0.0.1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ChildClientChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new NettyTimeClientHandler5());
    }
}
