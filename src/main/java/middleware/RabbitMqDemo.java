package middleware;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * @author jinzhimin
 * @description: 测试RabbitMQ。
 */
public class RabbitMqDemo {
    private static Logger logger = LoggerFactory.getLogger(RabbitMqDemo.class);

    private static final String EXCHANGE_NAME = "exchange.test";

    public final static String QUEUE_NAME = "rabbitMQ.test";

    /**
     * 创建连接工厂
     */
    private static ConnectionFactory factory = new ConnectionFactory();

    static {
        // 设置RabbitMQ相关信息
        factory.setHost("localhost");
    }

    /**
     * 路由关键字
     */
    private static final String[] routingKeys = new String[]{"info", "warning", "error"};

    public static void closeRabbitMq(Channel channel, Connection connection) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                logger.info("关闭通道出现IO异常！", e);
            } catch (TimeoutException e) {
                logger.info("关闭通道出现超时异常！", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                logger.info("关闭连接出现IO异常！", e);
            }
        }
    }

    public static void pushMsgIntoQueue(String queueName, String message) {
        Connection connection = null;
        Channel channel = null;
        try {
            // 创建一个新的连接
            connection = factory.newConnection();
            // 创建一个通道
            channel = connection.createChannel();
            // 声明一个队列
            channel.queueDeclare(queueName, false, false, false, null);
            // 发送消息到队列中
            while (true) {
                // ""空字符串的匿名交换机
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                logger.info("Producer Send +'" + message + "'");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        } finally {
            // 关闭通道和连接
            closeRabbitMq(channel, connection);
        }
    }

    public static void popMsgFromQueue(String queueName) {
        Connection connection = null;
        Channel channel = null;
        try {
            // 创建一个新的连接
            connection = factory.newConnection();
            // 创建一个通道
            channel = connection.createChannel();
            // 声明要关注的队列
            channel.queueDeclare(queueName, false, false, false, null);
            // 每次从队列获取的数量
            channel.basicQos(1);
            // DefaultConsumer类实现了Consumer接口，通过传入一个频道，
            // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.info("Customer Received '" + message + "'");
                }
            };
            // 自动回复队列应答 -- RabbitMQ中的消息确认机制
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        } finally {
            // 消费消息时，不要关闭通道和连接
        }
    }

    public static void popMsgManualAckQueue(String queueName){
        try {
            // 创建一个新的连接
            Connection connection = factory.newConnection();
            // 创建一个通道
            Channel channel = connection.createChannel();
            // 声明要关注的队列
            channel.queueDeclare(queueName, false, false, false, null);
            // 每次从队列获取的数量
            channel.basicQos(1);
            // DefaultConsumer类实现了Consumer接口，通过传入一个频道，
            // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.info("Customer Received '" + message + "'");
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        logger.info("出现中断异常！", e);
                    } finally{
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };
            // 不自动回复应答
            channel.basicConsume(queueName, false, consumer);
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        } finally {
            // 消费消息时，不要关闭通道和连接
        }
    }

    public static void pushFanoutExchange(String exchangeName) {
        Connection connection = null;
        Channel channel = null;
        try {
            // 创建一个新的连接
            connection = factory.newConnection();
            // 创建一个通道
            channel = connection.createChannel();
            // fanout表示分发，所有的消费者得到同样的队列信息
            channel.exchangeDeclare(exchangeName, "fanout");
            // 分发信息
            for (int i = 0; i < 5; i++) {
                String message = "Hello World" + i;
                channel.basicPublish(exchangeName, "", null, message.getBytes());
                logger.info("EmitLog Sent '" + message + "'");
            }
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        } finally {
            closeRabbitMq(channel, connection);
        }
    }

    public static void popFanoutExchange(String exchangeName) {
        Connection connection = null;
        Channel channel = null;
        try {
            // 创建一个新的连接
            connection = factory.newConnection();
            // 创建一个通道
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "fanout");

            // 产生一个随机的队列名称
            String queueName = channel.queueDeclare().getQueue();
            // 对队列进行绑定
            channel.queueBind(queueName, exchangeName, "");

            logger.info("ReceiveLogs1 Waiting for messages");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.info("ReceiveLogs1 Received '" + message + "'");
                }
            };
            // 队列会自动删除
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        }
    }

    public static void pushDirectExchange(String exchangeName) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            // 声明交换机,注意是direct
            channel.exchangeDeclare(exchangeName, "direct");
            // 发送信息
            for (String routingKey : routingKeys) {
                String message = "RoutingSendDirect level:" + routingKey;
                channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
                logger.info("RoutingSendDirect Send " + routingKey + "':'" + message);
            }
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        } finally {
            closeRabbitMq(channel, connection);
        }
    }

    public static void popDirectExchange(String exchangeName) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            // 声明交换器
            channel.exchangeDeclare(exchangeName, "direct");
            // 获取匿名队列名称
            String queueName = channel.queueDeclare().getQueue();

            //根据路由关键字进行绑定
            for (String routingKey : routingKeys) {
                channel.queueBind(queueName, exchangeName, routingKey);
                logger.info("ReceiveLogsDirect1 exchange:" + EXCHANGE_NAME + "," +
                        " queue:" + queueName + ", BindRoutingKey:" + routingKey);
            }
            logger.info("ReceiveLogsDirect1  Waiting for messages");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.info("ReceiveLogsDirect1 Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        }
    }

    public static void pushTopicExchange(String exchangeName){
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            // 声明一个匹配模式的交换机
            channel.exchangeDeclare(exchangeName, "topic");
            // 待发送的消息
            String[] routingKeys = new String[]{
                    "quick.orange.rabbit",
                    "lazy.orange.elephant",
                    "quick.orange.fox",
                    "lazy.brown.fox",
                    "quick.brown.fox",
                    "quick.orange.male.rabbit",
                    "lazy.orange.male.rabbit"
            };
            // 发送消息
            for (String severity : routingKeys) {
                String message = "From " + severity + " routingKey' s message!";
                channel.basicPublish(exchangeName, severity, null, message.getBytes());
                logger.info("TopicSend Sent '" + severity + "':'" + message + "'");
            }
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        } finally {
            closeRabbitMq(channel, connection);
        }
    }

    public static void popTopicExchange(String exchangeName){
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            // 声明一个匹配模式的交换器
            channel.exchangeDeclare(exchangeName, "topic");
            String queueName = channel.queueDeclare().getQueue();
            // 路由关键字
            String[] routingKeys = new String[]{"*.*.rabbit", "lazy.#"};
            // 绑定路由关键字
            for (String bindingKey : routingKeys) {
                channel.queueBind(queueName, exchangeName, bindingKey);
                logger.info("ReceiveLogsTopic2 exchange:" + exchangeName+ ", queue:" + queueName + ", BindRoutingKey:" + bindingKey);
            }

            logger.info("ReceiveLogsTopic2 Waiting for messages");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws UnsupportedEncodingException {
                    String message = new String(body, "UTF-8");
                    logger.info("ReceiveLogsTopic2 Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            logger.info("Rabbit MQ 出现IO异常！", e);
        } catch (TimeoutException e) {
            logger.info("Rabbit MQ 超时异常！", e);
        }
    }

    public static void testPushTopicExchange() {
        pushTopicExchange("topic." + EXCHANGE_NAME);
    }

    public static void testPopTopicExchange() {
        popTopicExchange("topic." + EXCHANGE_NAME);
    }

    public static void testPushDirectExchange() {
        pushDirectExchange("direct." + EXCHANGE_NAME);
    }

    public static void testPopDirectExchange() {
        popDirectExchange("direct." + EXCHANGE_NAME);
    }

    public static void testPushFanoutExchange() {
        pushFanoutExchange(EXCHANGE_NAME);
    }

    public static void testPopFanoutExchange() {
        popFanoutExchange(EXCHANGE_NAME);
    }

    public static void testProducer() {
        String message = "Hello RabbitMQ";
        pushMsgIntoQueue(QUEUE_NAME, message);
    }

    public static void testConsumer() {
        popMsgFromQueue(QUEUE_NAME);
    }

    public static void main(String[] args) {
//        testProducer();
//        testConsumer();
//        testPushFanoutExchange();
//        testPopFanoutExchange();
//        testPushDirectExchange();
//        testPopDirectExchange();
        testPushTopicExchange();
//        testPopTopicExchange();
    }


}
