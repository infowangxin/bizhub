package com.bizhub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bizhub.entity.Item;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    public void process(Item item) {
        log.debug("# ItemService.process() , {}", JSON.toJSONString(item));
        
        
        
    }

}
