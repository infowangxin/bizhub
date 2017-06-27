package com.bizhub.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bizhub.common.TopicEnum;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    // @Autowired
    // @Qualifier("producerChannel")
    // private MessageChannel channel;

    public void sendMessage(TopicEnum topic, String message) {
        logger.info("# topic = {}, message = {}", topic.getKey(), message);
        // kafkaTemplate.setDefaultTopic(topic.getKey());
        // kafkaTemplate.sendDefault(message);
        kafkaTemplate.send(topic.getKey(), message);
        // channel.send(MessageBuilder.withPayload(message).
        // setHeader("topic", topic.getKey()).
        // build());

    }

    public void sendMessage(TopicEnum topic, int key, String message) {
        logger.info("# topic = {}, message = {}", topic.getKey(), message);
        // kafkaTemplate.setDefaultTopic(topic.getKey());
        // kafkaTemplate.sendDefault(key, message);
        kafkaTemplate.send(topic.getKey(), key, message);
        // channel.send(MessageBuilder.withPayload(message).
        // setHeader("messageKey", key).
        // setHeader("topic", topic.getKey()).
        // build());
    }
}
