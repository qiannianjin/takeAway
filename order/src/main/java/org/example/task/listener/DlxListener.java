package org.example.task.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.task.service.TransMessageServices;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * @ClassName DlxListener
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 20:56
 * Version 1.0
 **/
@Component
@Slf4j
//这个注解，可以根据配置是否启用
@ConditionalOnProperty("moodymq.dlxEnabled")
public class DlxListener  implements ChannelAwareMessageListener {
    @Autowired
    TransMessageServices transMessageServices;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String messageBody = new String(message.getBody());
        log.error("dead letter! message:{}",messageBody);

        MessageProperties messageProperties = message.getMessageProperties();
        transMessageServices.messageDead(messageProperties.getMessageId(),
                messageProperties.getReceivedExchange(),
                messageProperties.getReceivedRoutingKey(),messageProperties.getConsumerQueue(),messageBody);
        //单条确认
        channel.basicAck(messageProperties.getDeliveryTag(),false);


    }
}
