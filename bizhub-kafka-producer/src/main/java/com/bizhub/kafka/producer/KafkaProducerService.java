package com.bizhub.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    // @Autowired
    // @Qualifier("producerChannel")
    // private MessageChannel channel;

    public void sendMessage(String topic, String data) {
        logger.info("the message is to be send by kafka is : topic = {}, data = {}", topic, data);
        kafkaTemplate.setDefaultTopic(topic);
        kafkaTemplate.sendDefault(data);
    }

    public void sendMessage(String topic, int key, String data) {
        logger.info("the message is to be send by kafka is : topic = {}, data = {}", topic, data);
        kafkaTemplate.setDefaultTopic(topic);
        kafkaTemplate.sendDefault(key, data);
    }
}
