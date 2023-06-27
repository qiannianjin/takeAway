package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName RabbitValue
 * @Description TODO
 * @Author whz
 * @Date 2023/6/23 15:41
 * Version 1.0
 **/
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitValue {
    private String address;
    private Integer port;
    private String username;
    private String password;
}
