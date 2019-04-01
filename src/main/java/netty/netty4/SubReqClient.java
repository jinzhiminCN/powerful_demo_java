package netty.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: 订购请求的客户端
 */
public class SubReqClient extends NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(SubReqClient.class);

    public SubReqClient(ChannelInitializer<SocketChannel> channelInitializer){
        super.channelInitializer = channelInitializer;
    }

    public static void main(String[] args) {
        SubReqClient subReqClient = new SubReqClient(new SubReqClientChannelInitializer());
        try {
            subReqClient.connect(8080, "127.0.0.1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SubReqClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new ObjectDecoder(1024,
                ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
        socketChannel.pipeline().addLast(new ObjectEncoder());
        socketChannel.pipeline().addLast(new SubReqClientHandler());
    }
}

class SubReqClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SubReqClientHandler.class);

    public SubReqClientHandler(){

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        for(int i = 0; i < 10; i++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i){
        SubscribeReq req = new SubscribeReq();
        req.setAddress("南京市江宁区");
        req.setPhoneNumber("13800010002");
        req.setProductName("Netty");
        req.setSubReqID(i);
        req.setUserName("zhangsan");
        return req;


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Receive server response: [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


