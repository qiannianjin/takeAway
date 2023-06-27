package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.common.TransMessageType;
import org.example.config.RabbitConfig;
import org.example.task.TransMessageSender;
import org.example.task.service.TransMessageServices;
import org.example.test.entity.OrderDetail;
import org.example.test.entity.TransMessage;
import org.example.test.service.OrderDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author whz
 * @Date 2023/6/23 18:07
 * Version 1.0
 **/
@RestController
@RequestMapping("/")
public class TestController {


    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    @Qualifier("rabbitTemplate3")
    private RabbitTemplate rabbitTemplate3;

    @Autowired
    @Qualifier("rabbitTemplate2")
    private RabbitTemplate rabbitTemplate2;

    @Autowired
    private  RabbitTemplate rabbitTemplate;
    @Resource
    private OrderDetailService orderDetailService;

    @Autowired
    TransMessageSender transMessageSender;

    @Autowired
    private ObjectMapper objectMapper;



@PostMapping("/test")
public void test() throws JsonProcessingException, InterruptedException {


    String test = "test";
    MessageProperties messageProperties = new MessageProperties();

    //设置消息的过期时间
    messageProperties.setExpiration("150000");
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setAccountId("null");
    orderDetail.setAddress("null");
    orderDetail.setDeliverymanId("11");
    //orderDetailService.save(orderDetail);
    orderDetailService.saveOrUpdate(orderDetail);

    String orderDetailString = objectMapper.writeValueAsString(orderDetail);
    Message message = new Message(orderDetailString.getBytes(),messageProperties);
    //Message message = new Message("hello".getBytes(),messageProperties);
    CorrelationData correlationData= new CorrelationData();
    correlationData.setId(orderDetail.getId().toString());




    //rabbitTemplate.send("exchange.order.restaurant","key.order",message,correlationData);
    //rabbitTemplate.send("exchange.order.restaurant","key.restaurant",message,correlationData);
    //
    //rabbitTemplate2.convertAndSend("exchange.order.restaurant","key.restaurant",orderDetail,correlationData);

    //rabbitTemplate.setReturnsCallback(()->{});
    //rabbitTemplate.setConfirmCallback();
    //rabbitTemplate.setMandatory();

    //transMessageServices.messageSendReady("exchange.order.restaurant","key.order","");

    //transMessageSender.send("exchange.order.restaurant","key.order","你好");
    transMessageSender.send("exchange.order.restauran","key.order","你好");



    System.out.println("发送成功");


}






}
