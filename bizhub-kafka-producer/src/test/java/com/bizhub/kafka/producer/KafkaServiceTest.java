package com.bizhub.kafka.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext-kafka.xml", "classpath*:spring/applicationContext.xml" })
public class KafkaServiceTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaServiceTest.class);

    @Autowired
    private KafkaService kafkaService;

    @Test
    public void testSaveNews() {
        try {

            String message = "hello kafka!";
            log.debug("#{}", message);
            kafkaService.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
