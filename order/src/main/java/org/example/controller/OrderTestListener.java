package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.task.listener.AbstractMessageListerner;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderTestListener
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 19:12
 * Version 1.0
 **/
@Component
@Slf4j
public class OrderTestListener extends AbstractMessageListerner {

    //不同的队列监听,有不同的实现，但都需要继承AbstractMessageListerner，然后注入到listenContainner,然后再和队列绑定。
    @Override
    public void receviceMessage(Message message) throws Exception {

      log.info("消息监听测试{}",message.getBody().toString());
      throw new Exception("测试异常");

    }



}
