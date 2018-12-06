package nio;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author jinzhimin
 * @description: ${description}
 * @email jinzhimin@youxin.com
 * @since 2018/12/6 11:13
 */
public class TestReactor {
    private static final Logger logger = LoggerFactory.getLogger(TestReactor.class);

    public void testConnect() throws Exception{
        // BIO 阻塞
        Socket socket = new Socket("10.255.6.49",4700);
        logger.info("连接成功");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 下面这种写法，不用关闭客户端，服务器端也是可以收到的
        {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("hi");
            printWriter.flush();
        }

        // 这种写法必须关闭客户端，服务器端才可以收到 NIO不用
        {
//        socket.getOutputStream().write(new byte[]{'h','i'});
//        socket.getOutputStream().flush();
            //必须关闭BIO服务器才能收到消息.NIO服务器不需要关闭
            //socket.close();
        }

        byte[] buf = new byte[2048];
        logger.info("准备读取数据~~");

        while(true){
            try {
                // 两种读取数据方式
                // 会阻塞
                int count = socket.getInputStream().read(buf);
                // 可以读取到数据 会阻塞,直到遇见\n
                String readFromServer = bufferedReader.readLine();
                logger.info("方式二： 读取数据：" + readFromServer);
                logger.info("方式一： 读取数据：" + new String(buf) + " count = " + count);
                Thread.sleep(1*1000);
            } catch (InterruptedException e) {
                logger.info("while 循环 sleep 出错!", e);
            }
        }
    }

    public void testNioServer(){
        Thread server = new Thread(new Reactor());
        server.start();

        while(true){
            try {
                Thread.sleep(3*1000);
            } catch (InterruptedException e) {
                logger.info("while 循环 sleep 出错!", e);
            }
        }
    }

    public void testBioServer(){
        Thread server = new Thread(new BioServer());
        server.start();

        while(true){
            try {
                Thread.sleep(3*1000);
            } catch (InterruptedException e) {
                logger.info("while 循环 sleep 出错!", e);
            }
        }
    }

    public static void main(String[] args) {
        TestReactor testReactor = new TestReactor();
//        testReactor.testNioServer();
//        testReactor.testBioServer();

        try {
            testReactor.testConnect();
        } catch (Exception e) {
            logger.info("测试连接出现异常！", e);
        }
    }

}
