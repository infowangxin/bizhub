package com.bizhub.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bizhub.entity.Item;
import com.bizhub.hbase.common.HbaseService;
import com.bizhub.spark.process.SparkService;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private SparkService sparkService;

    @Autowired
    private HbaseService bbaseService;

    public void process(Item item) {
        log.debug("# ItemService.process() , {}", JSON.toJSONString(item));
        Item i = null;
        try {
            i = sparkService.process(item);
        } catch (Exception e) {
            log.error("# spark 处理失败, {}", e.getLocalizedMessage());
        }
        if (i != null) {
            bbaseService.save(i);
        }
        
        List<Item> items = bbaseService.findAll(Item.class);
        if(CollectionUtils.isNotEmpty(items)){
            for (Item m : items) {
                log.debug("#{}",JSON.toJSONString(m));
            }
        }
    }

}
