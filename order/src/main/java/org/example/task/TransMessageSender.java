package org.example.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.task.service.TransMessageServices;
import org.example.test.entity.TransMessage;
import org.example.test.service.TransMessageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName Sender
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 13:17
 * Version 1.0
 **/
@Component
@Slf4j
public class TransMessageSender {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    TransMessageServices transMessageServices;


    public void send(String exchange,String routingKey,Object payload){
        log.info("send():exchange:{} routingKey{} payload:{}",exchange,routingKey,payload);
    try{
        ObjectMapper objectMapper = new ObjectMapper();
        String payloadStr = objectMapper.writeValueAsString(payload);
        TransMessage transMessage = transMessageServices.messageSendReady(exchange, routingKey, payloadStr);


        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.setMessageId(transMessage.getId());
        Message message = new Message(payloadStr.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(exchange,routingKey,message,new CorrelationData(transMessage.getId()));
        log.info("message sent,ID:{}",transMessage.getId());

    }catch (Exception e){
        log.error(e.getMessage(),e);
    }


    }






}
