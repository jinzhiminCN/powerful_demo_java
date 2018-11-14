package middleware;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author jinzhimin
 * @description: ActiveMQ测试
 */
public class ActiveMqDemo {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqDemo.class);

    private static final String brokerUrl = "failover://(tcp://10.255.33.51:61616)";

    private static ConnectionFactory connectionFactory = null;

    static{
        //1、创建工厂连接对象，需要制定ip和端口号
        try {
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        } catch (Exception e) {
            logger.info("创建ActiveMQ连接工厂出错！", e);
        }
    }

    public static void main(String[] args) {
        try {
//            testMqProducerQueue();
            testMqConsumerQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void testMqProducerQueue(){
        sendToQueue("queue.test", "queue.hello");
    }

    public static void testMqConsumerQueue(){
        consumeQueue("queue.test");
    }

    public static void testMqTopicProducer(){
        sendToTopic("topic.test", "topic.hello");
    }

    public static void testMqTopicConsumer(){
        consumeQueue("topic.test");
    }

    /**
     * 关闭ActiveMQ的session，connection。
     * @param session
     * @param connection
     */
    private static void closeActiveMQ(Session session, Connection connection){
        if(session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                logger.info("ActiveMQ Session 未正常关闭", e);
            }
        }
        if(connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                logger.info("ActiveMQ Connection 未正常关闭", e);
            }
        }
    }

    public static void sendToQueue(String queueName, String value) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            //1、使用连接工厂创建一个连接对象
            connection = connectionFactory.createConnection();
            //2、开启连接
            connection.start();
            //3、使用连接对象创建会话（session）对象
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //4、使用会话对象创建目标对象，包含queue和topic（一对一和一对多）
            Queue queue = session.createQueue(queueName);
            //5、使用会话对象创建生产者对象
            producer = session.createProducer(queue);
            //6、使用会话对象创建一个消息对象
            TextMessage textMessage = session.createTextMessage(value);
            //7、发送消息
            producer.send(textMessage);
        } catch (JMSException e) {
            logger.info("ActiveMQ 出现异常", e);
        } finally {
            //8、关闭资源
            if(producer != null) {
                try {
                    producer.close();
                } catch (JMSException e){
                    logger.info("ActiveMQ MessageProducer 未正常关闭", e);
                }
            }
            closeActiveMQ(session, connection);
        }
    }

    public static void consumeQueue(String queueName){
        ConsumerMessageListener consumerMessageListener = new ConsumerMessageListener();

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            //1、使用连接工厂创建一个连接对象
            connection = connectionFactory.createConnection();
            //2、开启连接
            connection.start();
            //3、使用连接对象创建会话（session）对象
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //4、使用会话对象创建目标对象，包含queue和topic（一对一和一对多）
            Queue queue = session.createQueue(queueName);
            //5、使用会话对象创建生产者对象
            consumer = session.createConsumer(queue);
            //6、向consumer对象中设置一个messageListener对象，用来接收消息
            consumer.setMessageListener(consumerMessageListener);
            logger.info(consumerMessageListener.getMsgContent());
        } catch (JMSException e){
            logger.info("ActiveMQ 出现异常", e);
        } finally {
            //7、关闭资源
            if(consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException e) {
                    logger.info("ActiveMQ MessageConsumer 未正常关闭", e);
                }
            }
            closeActiveMQ(session, connection);
        }
    }

    public static void sendToTopic(String topicName, String value){
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        MessageProducer producer = null;

        try {
            // 1、使用连接工厂创建一个连接对象
            connection = connectionFactory.createConnection();
            // 2、开启连接
            connection.start();
            // 3、使用连接对象创建会话（session）对象
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 4、使用会话对象创建目标对象，包含queue和topic（一对一和一对多）
            Topic topic = session.createTopic(topicName);
            // 5、使用会话对象创建生产者对象
            producer = session.createProducer(topic);
            // 6、使用会话对象创建一个消息对象
            TextMessage textMessage = session.createTextMessage(value);
            // 7、发送消息
            producer.send(textMessage);
        } catch (JMSException e) {
            logger.info("ActiveMQ 出现异常", e);
        } finally {
            // 8、关闭资源
            if(producer != null) {
                try {
                    producer.close();
                } catch (JMSException e){
                    logger.info("ActiveMQ MessageProducer 未正常关闭", e);
                }
            }
            closeActiveMQ(session, connection);
        }
    }

    public static void consumeTopic(String topicName) throws Exception{
        ConsumerMessageListener consumerMessageListener = new ConsumerMessageListener();

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            // 1、使用连接工厂创建一个连接对象
            connection = connectionFactory.createConnection();
            // 2、开启连接
            connection.start();
            // 3、使用连接对象创建会话（session）对象
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 4、使用会话对象创建目标对象，包含queue和topic（一对一和一对多）
            Topic topic = session.createTopic(topicName);
            // 5、使用会话对象创建生产者对象
            consumer = session.createConsumer(topic);
            // 6、向consumer对象中设置一个messageListener对象，用来接收消息
            consumer.setMessageListener(consumerMessageListener);
            // 7、程序等待接收用户消息
            System.in.read();
        } catch (JMSException e) {
            logger.info("ActiveMQ 出现异常", e);
        } catch (IOException e) {
            logger.info("出现IO异常", e);
        } finally {
            if(consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException e) {
                    logger.info("ActiveMQ MessageConsumer 未正常关闭", e);
                }
            }
            closeActiveMQ(session, connection);
        }
    }
}

class ConsumerMessageListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessageListener.class);

    private String msgContent;

    public ConsumerMessageListener(){

    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMsg = (TextMessage) message;
        try {
            msgContent = textMsg.getText();
            logger.info("receive message: " + msgContent );
        } catch (JMSException e) {
            logger.info("监听消息出现异常！", e);
        }
    }
}
