package kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description:
 */
public class KafkaConsumerDemo2 implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerDemo2.class);

  private KafkaConsumer<String, String> consumer;
  private ConsumerRecords<String, String> msgList;
  private String topic;
  private static final String GROUP_ID = "groupB";

  public KafkaConsumerDemo2(String topicName) {
    this.topic = topicName;
    init();
  }

  @Override
  public void run() {
    logger.info("---------开始消费---------");
    int messageNo = 1;
    List<String> list = new ArrayList<String>();
    List<Long> list2 = new ArrayList<Long>();
    try {
      for (; ; ) {
        msgList = consumer.poll(100);
        if (null != msgList && msgList.count() > 0) {
          for (ConsumerRecord<String, String> record : msgList) {
            if (messageNo % 10 == 0) {
              logger.info(
                  messageNo
                      + "=======receive: key = "
                      + record.key()
                      + ", value = "
                      + record.value()
                      + " offset==="
                      + record.offset());
            }
            list.add(record.value());
            list2.add(record.offset());
            messageNo++;
          }
          if (list.size() == 50) {
            // 手动提交
            consumer.commitSync();
            logger.info("成功提交" + list.size() + "条,此时的offset为:" + list2.get(49));
          } else if (list.size() > 50) {
            consumer.close();
            init();
            list.clear();
            list2.clear();
          }
        } else {
          Thread.sleep(1000);
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      consumer.close();
    }
  }

  private void init() {
    Properties props = new Properties();
    // kafka消费的的地址
    props.put("bootstrap.servers", "localhost:9092");
    // 组名 不同组名可以重复消费
    props.put("group.id", GROUP_ID);
    // 是否自动提交
    props.put("enable.auto.commit", "false");
    // 超时时间
    props.put("session.timeout.ms", "30000");
    // 一次最大拉取的条数
    props.put("max.poll.records", 10);
    //		earliest当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
    //		latest
    //		当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
    //		none
    //		topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
    props.put("auto.offset.reset", "earliest");
    // 序列化
    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());
    this.consumer = new KafkaConsumer<String, String>(props);
    // 订阅主题列表topic
    this.consumer.subscribe(Arrays.asList(topic));

    logger.info("初始化!");
  }

  public static void main(String args[]) {
    KafkaConsumerDemo2 test1 = new KafkaConsumerDemo2("kafka_test");
    Thread thread1 = new Thread(test1);
    thread1.start();
  }
}
