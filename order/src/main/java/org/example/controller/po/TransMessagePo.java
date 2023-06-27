package org.example.controller.po;

import lombok.Data;
import org.example.common.TransMessageType;

import java.util.Date;

/**
 * @ClassName TransMessagePo
 * @Description TODO
 * @Author whz
 * @Date 2023/6/27 9:23
 * Version 1.0
 **/
@Data
public class TransMessagePo {

    private String id;//uuid
    private String service;
    private TransMessageType Type;
    private String exchange;
    private String routngKey;
    private String queue;
    private String sequence;
    private String playLoad;
    private Date date;

}
