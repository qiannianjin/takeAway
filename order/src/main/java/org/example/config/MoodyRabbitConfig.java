package org.example.config;


import lombok.extern.slf4j.Slf4j;
import org.example.controller.OrderTestListener;
import org.example.task.service.TransMessageServices;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MoodyRabbitConfig
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 7:27
 * Version 1.0
 **/
@Configuration
@Slf4j
public class MoodyRabbitConfig {

    @Value("${moodymq.host}")
    String host;
    @Value("${moodymq.port}")
    int port;
    @Value("${moodymq.username}")
    String username;
    @Value("${moodymq.password}")
    String password;
    @Value("${moodymq.vhost}")
    String vhost;

    @Autowired
    TransMessageServices transMessageServices;
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer2(ConnectionFactory connectionFactory, OrderTestListener orderTestListener){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMessageListener(orderTestListener);
        container.setQueueNames("queue.order");
        container.setPrefetchCount(1);
        container.setConcurrentConsumers(3);
        container.setMaxConcurrentConsumers(10);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //container.setMessagePropertiesConverter((MessagePropertiesConverter) new Jackson2JsonMessageConverter());
        return container;
    }



    @Bean
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        //放回消息确认带有id
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        connectionFactory.createConnection();

        return connectionFactory;

    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMaxConcurrentConsumers(10);
        factory.setConcurrentConsumers(3);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //托管
        rabbitTemplate.setMandatory(true);
        //发送失败的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息无法路由！message:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey:{}",
                    message,
                    replyCode,
                    replyText,
                    exchange,
                    routingKey);
            transMessageServices.messageSendReturn(message.getMessageProperties().getMessageId(),
                    exchange,routingKey,
                    new String(message.getBody()));

        });
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("correlationData:{}, ack:{},cause:{}", correlationData, ack, cause);
                if (ack && null != correlationData) {
                    String messageId = correlationData.getId();
                    log.info("消息已经正确投递到交换机，id：{}",messageId);
                    transMessageServices.messageSendSuccess(messageId);

                }else{
                    log.error("消息投递到交换机失败,correlationData:{}",correlationData);
                }
            }
        });
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }


}
