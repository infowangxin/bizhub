package com.bizhub.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bizhub.common.TopicEnum;
import com.bizhub.entity.Item;
import com.bizhub.kafka.producer.KafkaProducerService;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    
    @Autowired
    private KafkaProducerService kafkaService;

    @RequestMapping(value = { "/", "index" })
    String home() {
        log.info("# 进入默认首页");
        return "index";
    }
    
    @RequestMapping(value = "send", method = RequestMethod.POST)
    @ResponseBody String send() {
        Item item = new Item();
        item.setItemId("0222000300000000003");
        item.setBrandName("波司登");
        item.setBusiName("波司登");
        item.setGoodsName("波司登T恤");
        String message = JSON.toJSONString(item);
        log.debug("#{}", message);
        // kafkaService.sendMessage(TopicEnum.SALES, message);
        
        kafkaService.sendMessage(TopicEnum.ITEM, message);
        return "已发送";
    }

    @RequestMapping(value = "leftnav", method = RequestMethod.GET)
    String leftnav() {
        return "leftnav";
    }

    @RequestMapping(value = "topnav", method = RequestMethod.GET)
    String topnav() {
        return "topnav";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/error", method = { RequestMethod.POST, RequestMethod.GET })
    String error(HttpServletRequest request, ModelMap map) {
        Object err = request.getAttribute("err");
        if (err != null) {
            log.error("# err={}", err);
            map.put("err", err);
        }
        Object pageUrl = request.getAttribute("pageUrl");
        if (pageUrl != null) {
            log.error("# pageUrl={}", pageUrl);
            map.put("pageUrl", pageUrl);
        }
        Map<String, String[]> param = request.getParameterMap();
        if (MapUtils.isNotEmpty(param)) {
            for (Map.Entry<String, String[]> entry : param.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
                log.info("# error parameter.name=[{}],parameter.value=[{}]", entry.getKey(), entry.getValue());
            }
        }
        log.info("# 进入错误页面");
        return "common/error";
    }

}
