package org.example.config;

import com.rabbitmq.client.Channel;

import jdk.nashorn.internal.objects.annotations.Property;
import org.example.controller.MyQueueListener;
import org.example.test.entity.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @ClassName RabbitConfig
 * @Description TODO
 * @Author whz
 * @Date 2023/6/23 9:56
 * Version 1.0
 **/
@Configuration
public class RabbitConfig {
    //@Value("${spring.rabbitmq.address}")
    //private String address;
    //@Value("${spring.rabbitmq.port}")
    //private Integer port;
    //@Value("${spring.rabbitmq.username}")
    //private String username;
    //@Value("${spring.rabbitmq.password}")
    //private String password;

    private final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);
    @Autowired
    private RabbitValue rabbitValue;

    //@Autowired
    //MyQueueListener myQueueListener;
    @Autowired
    MessageConverter converter;

    //@Bean
    //public RabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory){
    //
    //    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
    //    simpleRabbitListenerContainerFactory.setConnectionFactory(cachingConnectionFactory);
    //    simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    //    simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(1);
    //    return simpleRabbitListenerContainerFactory;
    //}


    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }





    //@Bean
    //public MessageListenerContainer messageListenerContainer(CachingConnectionFactory connectionFactory){
    //    SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
    //    simpleMessageListenerContainer.setQueueNames("queue.order","queue.restaurant");
    //    simpleMessageListenerContainer.setConcurrentConsumers(3);
    //    simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
    //    //确认模式(自动确认)
    //    //simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
    //    //simpleMessageListenerContainer.setMessageListener(new MessageListener() {
    //    //    @Override
    //    //    public void onMessage(Message message) {
    //    //        logger.info("message:{}",message);
    //    //    }
    //    //});
    //    //手动确认
    //    simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    //    //simpleMessageListenerContainer.setMessageListener(new ChannelAwareMessageListener() {
    //    //    @Override
    //    //    public void onMessage(Message message, Channel channel) throws Exception {
    //    //        //channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    //    //        System.out.println(message);
    //    //    }
    //    //});
    //    //每次只预取一条消息，消费端限流。
    //    simpleMessageListenerContainer.setPrefetchCount(1);
    //
    //
    //
    //    //simpleMessageListenerContainer.setMessageListener(myQueueListener);
    //
    //    MessageListenerAdapter messageListenerAdapter=new MessageListenerAdapter();
    //    messageListenerAdapter.setDelegate(myQueueListener);
    //    HashMap<String,String> map = new HashMap<>();
    //    map.put("queue.order","handleMessage");
    //    map.put("queue.restaurant","handleMessage2");
    //    messageListenerAdapter.setQueueOrTagToMethodName(map);
    //    Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
    //    jackson2JsonMessageConverter.setClassMapper(new ClassMapper() {
    //        @Override
    //        public void fromClass(Class<?> aClass, MessageProperties messageProperties) {
    //
    //        }
    //
    //        @Override
    //        public Class<?> toClass(MessageProperties messageProperties) {
    //            return OrderDetail.class;
    //        }
    //    });
    //    messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
    //    simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);
    //
    //
    //    //jackson2jsonMesssageConverter
    //    return simpleMessageListenerContainer;
    //}





    @Bean(name = "rabbitTemplate2")
    public RabbitTemplate rabbitTemplate2(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //托管
        rabbitTemplate.setMandatory(true);
        //发送失败的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey)->{
            logger.info("message:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey:{}" ,
                    message,
                    replyCode,
                    replyText,
                    exchange,
                    routingKey);
        });
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("第二种template");
                logger.info("correlationData:{}, ack:{},cause:{}",correlationData,b,s);
            }
        });
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

   @Bean(name = "rabbitTemplate3")
    public RabbitTemplate rabbitTemplate3(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //托管
        rabbitTemplate.setMandatory(true);
        //发送失败的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey)->{
            logger.info("message:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey:{}" ,
                    message,
                    replyCode,
                    replyText,
                    exchange,
                    routingKey);
        });
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("第一种默认template");
                logger.info("correlationData:{}, ack:{},cause:{}",correlationData,b,s);
            }
        });
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    //@Bean
    //public CachingConnectionFactory connectionFactory() {
    //    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    //    connectionFactory.setHost(rabbitValue.getAddress());
    //    connectionFactory.setPort(rabbitValue.getPort());
    //    connectionFactory.setPassword(rabbitValue.getPassword());
    //    connectionFactory.setUsername(rabbitValue.getUsername());
    //    //设置发送确认和接收确认
    //    connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
    //    connectionFactory.setPublisherReturns(true);
    //    connectionFactory.createConnection();
    //    return connectionFactory;
    //}

    //
    //@Bean
    //public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
    //    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    //    rabbitAdmin.setAutoStartup(true);
    //    return rabbitAdmin;
    //}
    ///*---------------restaurant---------------*/
    //@Bean
    //public Exchange exchange() {
    //    return new DirectExchange("exchange.order.restaurant");
    //}
    //
    //@Bean
    //public Queue queue() {
    //    return new Queue("queue.order");
    //}
    //@Bean
    //public Queue queue2() {
    //    return new Queue("queue.restaurant");
    //}
    //@Bean
    //public Binding binding() {
    //    return new Binding("queue.order",
    //            Binding.DestinationType.QUEUE,
    //            "exchange.order.restaurant",
    //            "key.order",
    //            null);
    //}
    //@Bean
    //public Binding binding4() {
    //    return new Binding("queue.restaurant",
    //            Binding.DestinationType.QUEUE,
    //            "exchange.order.restaurant",
    //            "key.restaurant",
    //            null);
    //}
    //
    ///*-----------------deliveryman------------------*/
    //@Bean
    //public Exchange exchange1() {
    //    return new DirectExchange("exchange.order.deliveryman");
    //}
    //@Bean
    //public Binding binding1() {
    //    return new Binding("queue.order",
    //            Binding.DestinationType.QUEUE,
    //            "exchange.order.deliveryman",
    //            "key.order",
    //            null);
    //}
    ///*--------------settlement---------------------*/
    ////这个有连个settlementexchange
    //@Bean
    //public Exchange exchange2() {
    //    return new FanoutExchange("exchange.order.settlement");
    //}
    //@Bean
    //public Exchange exchange3() {
    //    return new FanoutExchange("exchange.settlement.order");
    //}
    //@Bean
    //public Binding binding2() {
    //    return new Binding("queue.order",
    //            Binding.DestinationType.QUEUE,
    //            "exchange.settlement.order",
    //            "key.order",
    //            null);
    //}
    ///*---------------reward------------------------*/
    //@Bean
    //public Exchange exchange4() {
    //    return new TopicExchange("exchange.order.reward");
    //}
    //@Bean
    //public Binding binding3() {
    //    return new Binding(
    //            "queue.order",
    //            Binding.DestinationType.QUEUE,
    //            "exchange.order.reward",
    //            "key.order",
    //            null);
    //}

    public void initRabbit() {


        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitValue.getAddress());
        connectionFactory.setPort(rabbitValue.getPort());
        connectionFactory.setPassword(rabbitValue.getPassword());
        connectionFactory.setUsername(rabbitValue.getUsername());

        System.out.println(rabbitValue);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);

        /*---------------restaurant---------------*/
        Exchange exchange = new DirectExchange("exchange.order.restaurant");
        rabbitAdmin.declareExchange(exchange);
        Queue queue = new Queue("queue.order");
        rabbitAdmin.declareQueue(queue);
        Binding binding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.restaurant",
                "key.order",
                null);
        rabbitAdmin.declareBinding(binding);

        /*-----------------deliveryman------------------*/
        exchange = new DirectExchange("exchange.order.deliveryman");
        rabbitAdmin.declareExchange(exchange);
        queue = new Queue("queue.order");
        rabbitAdmin.declareQueue(queue);
        binding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.deliveryman",
                "key.order",
                null);
        rabbitAdmin.declareBinding(binding);


        /*--------------settlement---------------------*/
        //这个有连个settlementexchange
        exchange = new FanoutExchange("exchange.order.settlement");
        rabbitAdmin.declareExchange(exchange);

        exchange = new FanoutExchange("exchange.settlement.order");
        rabbitAdmin.declareExchange(exchange);
        queue = new Queue("queue.order");
        rabbitAdmin.declareQueue(queue);
        binding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.settlement.order",
                "key.order",
                null);
        rabbitAdmin.declareBinding(binding);

        /*---------------reward------------------------*/
        exchange = new TopicExchange("exchange.order.reward");
        rabbitAdmin.declareExchange(exchange);
        queue = new Queue("queue.order");
        rabbitAdmin.declareQueue(queue);
        binding = new Binding(
                "queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.reward",
                "key.order",
                null);
        rabbitAdmin.declareBinding(binding);


    }


}
