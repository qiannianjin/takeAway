package org.example.task.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.task.service.TransMessageServices;
import org.example.test.entity.TransMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * @ClassName AbstractMessageListerner
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 18:38
 * Version 1.0
 **/
@Slf4j
public abstract class AbstractMessageListerner implements ChannelAwareMessageListener {

    @Autowired
    TransMessageServices transMessageServices;

    public abstract void receviceMessage(Message message) throws Exception;

    @Value("${moodymq.resendTimes}")
    int resendTimes;
    @Override
    public void onMessage(Message message, Channel channel) throws IOException, InterruptedException {
        MessageProperties messageProperties= message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        //接受到消息先入库
        TransMessage transMessage = transMessageServices.messageReceiveReady(messageProperties.getMessageId(),
                messageProperties.getReceivedExchange(),
                messageProperties.getReceivedRoutingKey(),
                messageProperties.getConsumerQueue(),
                new String(message.getBody()));

        log.info("收到消息{}，消费次数{}",messageProperties.getMessageId(),transMessage.getSequence());
        try{
                //业务代码强制逻辑编写
                receviceMessage(message);
                //如果业务没有异常，确认消息被消费
                channel.basicAck(deliveryTag,false);
                //删除数据库
                transMessageServices.messageReciveSuccess(messageProperties.getMessageId());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            if(transMessage.getSequence()>resendTimes){
                //不然它重回队列，如果配置，可能会进入死信队列
                channel.basicReject(deliveryTag,false);
            }else{
                //重回队列，这个重回时间会根据次数成指数增长

                Thread.sleep((long) Math.pow(2,transMessage.getSequence())*1000);

                channel.basicNack(deliveryTag,false,true);
            }


        }
    }
}
