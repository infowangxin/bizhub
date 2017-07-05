package com.bizhub.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "send", method = RequestMethod.POST)
    @ResponseBody
    String send(HttpServletRequest request) {
        String serverName = request.getServerName();
        Integer serverPort = request.getServerPort();
        String serverPath = request.getContextPath();

        String localName = request.getLocalName();
        String localAddr = request.getLocalAddr();
        Integer localPort = request.getLocalPort();

        String remoteHost = request.getRemoteHost(); // 客户端主机名
        String remoteAddr = request.getRemoteAddr(); // 客户端ip地址
        Integer remotePort = request.getRemotePort(); // 客户端端口号

        String protocol = request.getProtocol();
        String schema = request.getScheme();

        String method = request.getMethod();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String queryString = request.getQueryString();

        String requestURI = request.getRequestURI();
        String requestURL = request.getRequestURL().toString();

        // 构造 map
        Map<String, Object> map = new HashMap<>();
        // map.put("requestHeaderMap", requestHeaderMap);
        // map.put("responseHeaderMap", responseHeaderMap);
        map.put("serverName", serverName);
        map.put("serverPort", serverPort);
        map.put("serverPath", serverPath);
        map.put("localName", localName);
        map.put("localAddr", localAddr);
        map.put("localPort", localPort);
        map.put("remoteHost", remoteHost);
        map.put("remoteAddr", remoteAddr);
        map.put("remotePort", remotePort);
        map.put("protocol", protocol);
        map.put("schema", schema);
        map.put("method", method);
        map.put("parameterMap", parameterMap);
        map.put("queryString", queryString);
        map.put("requestURI", requestURI);
        map.put("requestURL", requestURL);
        map.put("visitDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime()));

        String message = JSON.toJSONString(map);
        log.debug("#{}", message);
        // kafkaService.sendMessage(TopicEnum.SALES, message);

        kafkaService.sendMessage(TopicEnum.ITEM, message);
        return "send success";
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
