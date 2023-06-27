package org.example.test.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import org.example.common.TransMessageType;

/**
 * (TransMessage)表实体类
 *
 * @author makejava
 * @since 2023-06-27 12:29:33
 */
@SuppressWarnings("serial")
@Data
@Builder
public class TransMessage extends Model<TransMessage> {

    private String id;
    //服务名称
    private String service;
    //消息类型
    private TransMessageType type;
    //交换机
    private String exchange;
    //路由键
    private String routingKey;
    //队列名
    private String queue;
    //序列
    private Integer sequence;
    //消息体
    private String payload;
    //创建时间
    private Date date;


    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

