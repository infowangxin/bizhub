package com.bizhub.kafka.listener.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

import com.alibaba.fastjson.JSON;
import com.bizhub.entity.Item;
import com.bizhub.service.ItemService;

public class ItemMessageListener implements MessageListener<String, String> {

    protected final Logger log = LoggerFactory.getLogger(ItemMessageListener.class);

    @Autowired
    private ItemService itemService;

    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("=============ItemMessageListener开始消费=============");
        String topic = record.topic();
        String key = record.key();
        String value = record.value();
        long offset = record.offset();
        int partition = record.partition();
        log.info("-------------topic:" + topic);
        log.info("-------------value:" + value);
        log.info("-------------key:" + key);
        log.info("-------------offset:" + offset);
        log.info("-------------partition:" + partition);
        log.info("~~~~~~~~~~~~~ItemMessageListener消费结束~~~~~~~~~~~~~");
        Item item = JSON.parseObject(record.value(), Item.class);
        itemService.process(item);

    }

}
