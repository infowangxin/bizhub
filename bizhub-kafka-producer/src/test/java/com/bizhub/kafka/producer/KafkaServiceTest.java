package com.bizhub.kafka.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.bizhub.common.TopicEnum;
import com.bizhub.entity.Item;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext-kafka.xml", "classpath*:spring/applicationContext.xml" })
public class KafkaServiceTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaServiceTest.class);

    @Autowired
    private KafkaProducerService kafkaService;

    @Test
    public void testSendMessage() {
        try {
            Item item = new Item();
            item.setItemId("0222000300000000003");
            item.setBrandName("波司登");
            item.setBusiName("波司登");
            item.setGoodsName("波司登T恤");
            String message = JSON.toJSONString(item);
            log.debug("#{}", message);
            // kafkaService.sendMessage(TopicEnum.SALES, message);
            kafkaService.sendMessage(TopicEnum.ITEM, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
