package org.example.task.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.val;
import org.example.common.TransMessageType;
import org.example.test.entity.TransMessage;
import org.example.test.service.TransMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName TransMessageServiceImpl
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 12:40
 * Version 1.0
 **/
@Component
public class TransMessageServiceImpl implements TransMessageServices {
    @Autowired
   TransMessageService transMessageService;
    @Value("${moodymq.service}")
    String serviceName;


    @Override
    public TransMessage messageSendReady(String exchange, String routingKey, String body) {
        final String messageId = UUID.randomUUID().toString();
        TransMessage transMessage = TransMessage.builder().build();
        transMessage.setId(messageId);
        transMessage.setService(serviceName);
        transMessage.setPayload(body);
        transMessage.setQueue(null);
        transMessage.setExchange(exchange);
        transMessage.setType(TransMessageType.SEND);
        transMessage.setRoutingKey(routingKey);
        transMessage.setDate(new Date());
        transMessage.setSequence(0);
        transMessageService.saveOrUpdate(transMessage);
        return transMessage;
    }

    @Override
    public void messageSendSuccess(String id) {
        TransMessage transMessage = TransMessage.builder().build();
        transMessage.setId(id);
        transMessage.setService(serviceName);
        transMessageService.remove(new QueryWrapper<>(transMessage));
    }

    @Override
    public TransMessage messageSendReturn(String id, String exchange, String routingKey, String body) {


        return messageSendReady(exchange,routingKey,body);
    }

    @Override
    public List<TransMessage> listReadyMessages() {
        //TransMessage transMessage = TransMessage.builder().service(serviceName).type(TransMessageType.SEND).build();

        return transMessageService.list(new QueryWrapper<>(TransMessage.builder().service(serviceName).type(TransMessageType.SEND).build()));
    }

    @Override
    public void messageResend(String id) {

        TransMessage one = transMessageService.getOne(new QueryWrapper<>(TransMessage.builder().id(id).service(serviceName).build()));

        //建议用分布是锁
        one.setSequence(one.getSequence()+1);
        transMessageService.saveOrUpdate(one);



    }

    @Override
    public void messageDead(String id) {
        TransMessage one = transMessageService.getOne(new QueryWrapper<>(TransMessage.builder().id(id).service(serviceName).build()));
        one.setType(TransMessageType.DEAD);
        transMessageService.saveOrUpdate(one);
    }

    @Override
    public void messageDead(String id, String exchange, String routingKey, String queue, String body) {

        TransMessage transMessage = TransMessage.builder()
                .id(id)
                .service(serviceName)
                .type(TransMessageType.DEAD)
                .exchange(exchange)
                .date(new Date())
                .routingKey(routingKey)
                .queue(queue)
                .payload(body)
                .sequence(0)
                .build();
            transMessageService.saveOrUpdate(transMessage);
    }

    @Override
    public TransMessage messageReceiveReady(String id, String exchange, String routingKey, String queue, String body) {
        TransMessage transMessage = TransMessage.builder()
                .id(id)
                .service(serviceName)
                .type(TransMessageType.RECEIVE)
                .exchange(exchange)
                .date(new Date())
                .routingKey(routingKey)
                .queue(queue)
                .payload(body)
                .sequence(0)
                .build();
        //查询
       TransMessage transMessage2 = transMessageService.getOne(new QueryWrapper<>(TransMessage.builder().id(id).service(serviceName).build()));
        if(transMessage2 == null){

            boolean b = transMessageService.saveOrUpdate(transMessage);
            return transMessage;
        }else{
            transMessage2.setSequence(transMessage2.getSequence()+1);
            transMessageService.saveOrUpdate(transMessage2);
            return transMessage2;
        }


    }

    @Override
    public void messageReciveSuccess(String id) {
            transMessageService.removeById(id);
    }
}
