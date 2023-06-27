package org.example.task;

import lombok.extern.slf4j.Slf4j;
import org.example.task.service.TransMessageServices;
import org.example.test.entity.TransMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ResendTask
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 11:48
 * Version 1.0
 **/
@EnableScheduling
@Configuration
@Component
@Slf4j
public class ResendTask
{
    @Autowired
    TransMessageServices transMessageServices;

    @Value("${moodymq.resendTimes}")
    Integer resendTimes;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelayString = "${moodymq.resendFreq}")
    public void resendMessage(){

            log.info("resendMessage() invoked.");
        List<TransMessage> transMessages = transMessageServices.listReadyMessages();
        log.info("resendMessage() :messagepos:{}",transMessages);
        for(TransMessage transMessage :transMessages){
            if(transMessage.getSequence()>resendTimes){
                log.info("resend too many times");
                transMessageServices.messageDead(transMessage.getId());
                continue;
            }

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");
            messageProperties.setMessageId(transMessage.getId());
            Message message = new Message(transMessage.getPayload().getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(transMessage.getExchange(),transMessage.getRoutingKey(),message,new CorrelationData(transMessage.getId()));
            log.info("message sent,ID:{}",transMessage.getId());
            //重发的话，sequence也要加1
            transMessageServices.messageResend(transMessage.getId());

        }






    }
}
