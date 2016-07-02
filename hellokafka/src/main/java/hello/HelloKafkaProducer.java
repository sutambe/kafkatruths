package hello;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback; 
import java.util.Properties;

public class HelloKafkaProducer {
  public static void main(String[] args) {
    String hello = "Hello, Kafka!";
    System.out.println(hello);

    HelloKafkaProducer producer = new HelloKafkaProducer();
    producer.produce();
  }

  void produce() {

    try {
      String topicName = "sut-hello-topic-v1"; 
      Properties props = new Properties();
      props.put("bootstrap.servers", "localhost:1056");
      props.put("acks", "all");
      props.put("retries", 0);
      props.put("batch.size", 16384);
      props.put("linger.ms", 1);
      props.put("buffer.memory", 33554432);
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

      Producer<String, String> producer = new KafkaProducer<String, String>(props);

      for(int i = 0;i < 1000; i++)
      {
        final int recordNum = i;
        System.out.println("Sending " + i);

        ProducerRecord<String, String> record = 
            new ProducerRecord<String, String>(
              topicName, 
              Integer.toString(i), 
              "Barak Obama: " + Integer.toString(recordNum));

        Callback callback = 
          new Callback() {
             public void onCompletion(RecordMetadata metadata, Exception e) {
               if(e != null)
                 e.printStackTrace();
               System.out.println("The offset of the record #" + recordNum + " is: " + metadata.offset());
              }
          };
        
        producer.send(record, callback);

        Thread.sleep(50);
      }

      producer.close();
    }
    catch(InterruptedException ex)
    {
      System.out.println("Thread got interrupeted: " + ex);
    }
  }

}
