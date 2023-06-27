package org.example.task.service;

import org.example.test.entity.TransMessage;

import java.util.List;

public interface TransMessageServices {

    //消息发送前
    TransMessage messageSendReady(String exchange, String routingKey, String body);
    //消息发送成功
    void messageSendSuccess(String id);

    //设置消息返回
    TransMessage messageSendReturn(String id,String exchange,String routingKey,String body);
    //查询应发未发消息
    List<TransMessage> listReadyMessages();

    //记录消息的重发次数
    void messageResend(String id);

    //消息重发多次，放弃
    void messageDead(String id);

}
