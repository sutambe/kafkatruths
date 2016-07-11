package hello;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import java.util.Properties;

public class HelloKafkaConsumer {
  public static void main(String[] args) {
    HelloKafkaConsumer consumer = new HelloKafkaConsumer();
    consumer.consume();
  }

  void consume() {
    System.out.println("Beginning consumption");

    String topicName = "sut-hello-topic-v1"; 
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:1056");
    props.put("group.id", "test");
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
    consumer.subscribe(Arrays.asList(topicName));

    try {
      final int minBatchSize = 2;
      List<ConsumerRecord<String, String>> buffer = new ArrayList<>();

      while(true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for(ConsumerRecord<String, String> record : records)
          buffer.add(record);

        if (buffer.size() >= minBatchSize) {
          insertIntoDb(buffer);
          consumer.commitSync();
          buffer.clear();
        }                                 
      }

    }
    catch(Exception ex)
    {
      System.out.println("Got an exception: " + ex);
      consumer.close();
    }
  }

  static void insertIntoDb(List<ConsumerRecord<String, String>> buffer) {
    for(ConsumerRecord<String, String> record : buffer) {
      System.out.println("Key = " + record.key() + 
                         ", Value = " + record.value()); 
      System.out.flush();
    }
  }
}
