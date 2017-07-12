package com.bizhub.interceptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.bizhub.common.TopicEnum;
import com.bizhub.kafka.producer.KafkaProducerService;

@SuppressWarnings("all")
public class KafkaHandlerInterceptor implements HandlerInterceptor {

    public static final Logger log = LoggerFactory.getLogger(KafkaHandlerInterceptor.class);

    @Autowired
    private KafkaProducerService kafkaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

        // map.put("requestHeaderMap", request.getParameterMap());
        // map.put("responseHeaderMap", request.getParameterMap());
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

        String message = JSON.toJSONString(map, true);
        log.info("#{}", message);
        kafkaService.sendMessage(TopicEnum.ITEM, message);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
