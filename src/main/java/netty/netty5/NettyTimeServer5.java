package netty.netty5;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import netty.NettyServer;
import netty.netty4.NettyTimeServer4;

/**
 * @author jinzhimin
 * @description: Netty 5 版本的时间服务器
 */
public class NettyTimeServer5 extends NettyServer{

    public NettyTimeServer5(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        NettyTimeServer4 timeServer = new NettyTimeServer4(new ChildServerChannelHandler());
        try {
            timeServer.bind(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ChildServerChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new NettyTimeServerHandler5());
    }
}