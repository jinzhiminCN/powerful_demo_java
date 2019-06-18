package kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description:
 */
public class KafkaProducerDemo implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(KafkaProducerDemo.class);

  private final KafkaProducer<String, String> producer;
  private final String topic;

  public KafkaProducerDemo(String topicName) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    // 配置为大于0的值的话，客户端会在消息发送失败时重新发送。
    props.put("retries", 0);
    // 当多条消息需要发送到同一个分区时，生产者会尝试合并网络请求。这会提高client和生产者的效率
    props.put("batch.size", 16384);
    props.put("key.serializer", StringSerializer.class.getName());
    props.put("value.serializer", StringSerializer.class.getName());

    this.producer = new KafkaProducer<String, String>(props);
    this.topic = topicName;
  }

  @Override
  public void run() {
    int messageNo = 1;
    try {
      for (; ; ) {
        String messageStr = "你好，这是第" + messageNo + "条数据";
        producer.send(new ProducerRecord<String, String>(topic, "Message", messageStr));
        // 生产了10条就打印
        if (messageNo % 10 == 0) {
          logger.info("发送的信息:" + messageStr);
        }
        // 生产100条就退出
        if (messageNo % 100 == 0) {
          logger.info("成功发送了" + messageNo + "条");
          break;
        }
        messageNo++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      producer.close();
    }
  }

  public static void main(String args[]) {
    KafkaProducerDemo test = new KafkaProducerDemo("kafka_test");
    Thread thread = new Thread(test);
    thread.start();
  }
}
