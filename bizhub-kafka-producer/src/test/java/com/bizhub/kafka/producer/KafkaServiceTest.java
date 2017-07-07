package com.bizhub.kafka.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bizhub.common.TopicEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext-kafka.xml", "classpath*:spring/applicationContext.xml" })
public class KafkaServiceTest {

    private static final Logger log = LoggerFactory.getLogger("com.bizhub.kafka.producer.KafkaServiceTest");

    @Autowired
    private KafkaProducerService kafkaService;

    @Test
    public void testSendMessage() {
        try {

            while (true) {
                String message = "{\"serverPath\":\"\",\"localName\":\"[0:0:0:0:0:0:0:1]\",\"protocol\":\"HTTP/1.1\",\"requestURL\":\"http://localhost:8899/send\",\"visitDate\":\"2017-07-05 10:04:00.672\",\"serverName\":\"localhost\",\"remoteHost\":\"0:0:0:0:0:0:0:1\",\"schema\":\"http\",\"parameterMap\":{},\"serverPort\":8899,\"requestURI\":\"/send\",\"localPort\":8899,\"remotePort\":58953,\"method\":\"POST\",\"localAddr\":\"0:0:0:0:0:0:0:1\",\"remoteAddr\":\"0:0:0:0:0:0:0:1\"}";
                log.debug("{}", message);
                kafkaService.sendMessage(TopicEnum.ITEM, message);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
