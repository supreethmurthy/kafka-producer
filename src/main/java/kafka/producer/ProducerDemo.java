package kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import org.slf4j.Logger;

/**
 * @author supreethmurthy
 * @project kafka-producer
 */
public class ProducerDemo {

    private static Logger logger = LoggerFactory.getLogger(ProducerDemo.class);
    private static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            logger.error("Invalid input arguments. Topic and Value must be specified while key is optional");
            System.exit(-1);
        }
        String topicName = args[0];
        String key = "";
        String value = args[1];
        if (args.length == 3) {
            key = args[2];
        }
        //Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        //In this example, I am sending a String key and a String Value. Key and Value can be String, Avro etc
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record = null;
        if (key.trim().isEmpty()) {
            record = new ProducerRecord<String, String>(topicName, value);
        } else {
            record = new ProducerRecord<>(topicName, key, value);
        }
        // send data - asynchronous
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception ex) {
                // executes every time a message is successfully sent or an exception is thrown
                if (ex == null) {
                    // the record was successfully sent
                    logger.info("********* Data Published Successfully ********* \n" +
                                "TOPIC:" + recordMetadata.topic() + "\n" +
                                "PARTITION: " + recordMetadata.partition() + "\n" +
                                "OFFSET: " + recordMetadata.offset() + "\n" +
                                "TIMESTAMP: " + recordMetadata.timestamp());
                } else {
                    logger.error("Error occurred while publishing the message", ex);
                }
            }
        });
        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
    }
}
