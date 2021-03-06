package org.jmoney8080;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;

/**
 * @author jmonette
 */
public class OffSetter {

    @Option(name="--bootstrap-servers", required = true, usage = "The kafka servers to connect too.")
    private String bootstrapServersUrl;

    @Option(name="--consumer-group", required = true, usage = "The consumer group to change the offset.")
    private String consumerGroup;

    @Option(name="--topic", required = true, usage = "The topic in which the offset will be adjusted.")
    private String topic;

    @Option(name="--partition", required = true, usage = "The partition in which the offset will be adjusted.")
    private int partition;

    @Option(name="--offset", required = true, usage = "The offset to set on the topic partition for the consumer group.")
    private long offset;


    public static void main(String[] args) {
        new OffSetter().doMain(args);
    }

    private void doMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this, ParserProperties.defaults().withUsageWidth(80));

        try {
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            System.err.println(e.getMessage());
            System.err.println("java -jar OffSetter [options...] arguments...");
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println("  Example: java -jar OffSetter"+parser.printExample(ALL));

            return;
        }

        final Properties props = new Properties();
        props.put("group.id", consumerGroup);
        props.put("schema.registry.url", "dummy:dummy");
        props.put("bootstrap.servers", bootstrapServersUrl);
        props.put("auto.offset.reset", "none");
        props.put("key.deserializer", MockDeserialzier.class.getName());
        props.put("value.deserializer", MockDeserialzier.class.getName());

        TopicPartition topicPartition = new TopicPartition(topic, partition);
        Consumer consumer = new KafkaConsumer(props);
        consumer.assign(Collections.singleton(topicPartition));
        consumer.seek(topicPartition, offset);
        consumer.commitSync();

    }

    public static class MockDeserialzier implements Deserializer {

        @Override
        public void configure(Map map, boolean b) {

        }

        @Override
        public Object deserialize(String s, byte[] bytes) {
            return null;
        }

        @Override
        public void close() {

        }
    }
}
