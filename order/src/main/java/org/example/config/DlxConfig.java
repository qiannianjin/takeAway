package org.example.config;

import org.example.controller.OrderTestListener;
import org.example.task.listener.DlxListener;
import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DlxConfig
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 20:45
 * Version 1.0
 **/
@Configuration
@ConditionalOnProperty("moodymq.dlxEnabled")
public class DlxConfig {

    @Bean
    public TopicExchange dlxExchange(){
        return new TopicExchange("exchange.dlx");
    }
    @Bean
    public Queue dlxQueue(){
        return new Queue("queue.dlx",true,false,false);
    }
    @Bean
    public Binding dlxBinding(){
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("#");
    }


    //监听死信队列
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer3(ConnectionFactory connectionFactory, DlxListener dlxListener){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMessageListener(dlxListener);
        container.setQueueNames("queue.dlx");
        container.setPrefetchCount(1);
        container.setConcurrentConsumers(3);
        container.setMaxConcurrentConsumers(10);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //container.setMessagePropertiesConverter((MessagePropertiesConverter) new Jackson2JsonMessageConverter());
        return container;
    }

}




