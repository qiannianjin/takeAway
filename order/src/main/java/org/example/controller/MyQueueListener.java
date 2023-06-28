package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.example.test.entity.OrderDetail;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName MyQueueListener
 * @Description TODO
 * @Author whz
 * @Date 2023/6/24 11:11
 * Version 1.0
 **/
@Component
@RabbitListener(

        //containerFactory = "rabbitListenerContainerFactory",
        //admin = "rabbitAdmin",
        //这里我们直接创建bindings，这些binding会直接创建，如果binding里面的queue，exchange，在服务端是没有的，那么将
        bindings = {
                @QueueBinding(value = @Queue(name = "queue.restaurant"
                        //也可以定义队列的一些属性
                        //arguments = {
                        //        @Argument(name = "x-message-ttl",
                        //                value ="1000",
                        //        type = "java.lang.Interger"),
                        //        @Argument(name = "x-dead-letter-exchange",
                        //                value ="exchange.dlx"
                        //              )
                        //}
                ),
                        exchange = @Exchange(name = "exchange.order.restaurant", type = ExchangeTypes.DIRECT),
                        key = "key.restaurant")
                ,
                @QueueBinding(value = @Queue(name = "queue.order"),
                        exchange = @Exchange(name = "exchange.order.deliveryman", type = ExchangeTypes.DIRECT),
                        key = "key.order"),
                @QueueBinding(value = @Queue(name = "queue.order"),
                        exchange = @Exchange(name = "exchange.settlement.order", type = ExchangeTypes.FANOUT),
                        key = "key.order"),
                @QueueBinding(value = @Queue(name = "queue.order"),
                        exchange = @Exchange(name = "exchange.order.settlement", type = ExchangeTypes.FANOUT),
                        key = "key.order"),
                @QueueBinding(value = @Queue(name = "queue.order"),
                        exchange = @Exchange(name = "exchange.order.reward", type = ExchangeTypes.TOPIC),
                        key = "key.order")
        })
public class MyQueueListener {

    private ObjectMapper objectMapper = new ObjectMapper();


    //@Override
    //@RabbitListener(queues = "queue.order")
    //public void onMessage(Message message, Channel channel) throws Exception {
    //    // 消息处理逻辑
    //    byte[] body = message.getBody();
    //    String body1 = String.valueOf(body);
    //    System.out.println("body1 = " + body1);
    //}

    //@RabbitListener(queues = "queue.order")
    //public void onMessage(Message message) {
    //    // 消息处理逻辑
    //    byte[] body = message.getBody();
    //    String body1 = String.valueOf(body);
    //    System.out.println("body1 = " + body1);
    //}
    //@RabbitListener(ackMode = "MANUAL",
    //        queues = "queue.order",messageConverter = "converter")
    //public void handleMessage(@Payload Message message, Channel channel ,OrderDetail orderDetail) throws IOException {
    //
    //    long deliveryTag = message.getMessageProperties().getDeliveryTag();
    //    channel.basicAck(deliveryTag, false);// 手动确认消息
    //    System.out.println("orderDetail = " + orderDetail);
    //    System.out.println("queue.order:消息确认");
    //}

  //  @RabbitListener(queues = "queue.restaurant",ackMode = "AUTO",messageConverter = "converter")
    public  void onMessage2(@Payload Message message, Channel channel ,OrderDetail orderDetail) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //channel.basicAck(deliveryTag, false);// 手动确认消息
        System.out.println("object = " + orderDetail);
        System.out.println("queue.restaurant:消息确认");
        System.out.println("餐厅2");
    }


   // @RabbitListener(ackMode = "MANUAL", queues = "queue.order",messageConverter = "converter")
    public void handleMessage(@Payload Message message, Channel channel ,Object orderDetail) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);// 手动确认消息
        System.out.println("orderDetail = " + orderDetail);
        System.out.println("queue.order:消息确认");
    }

    //@RabbitListener()
    //public static void test(String[] args) {
    //
    //}




}
