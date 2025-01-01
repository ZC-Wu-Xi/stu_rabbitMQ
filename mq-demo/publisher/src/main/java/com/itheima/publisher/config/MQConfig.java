package com.itheima.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/30 14:20
 * @description
 * 每个`RabbitTemplate`只能配置一个`ReturnCallback`，
 * 因此我们可以在配置类中统一设置(一般我们来记录日志)
 */
@Slf4j
@Configuration
@RequiredArgsConstructor // 必需的构造函数
public class MQConfig {
    private final RabbitTemplate rabbitTemplate;

    // @PostConstruct 注解用于标记在一个类的实例被创建之后但在依赖注入（如构造函数或属性注入）完成之前需要被调用的方法。它通常用于进行初始化工作。在 Spring 中，结合 @PostConstruct 使用时，这个注解的方法会在 bean 初始化之后被自动调用。
    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("监听到了消息return callback");
            log.debug("交换机：{}", returned.getExchange());
            log.debug("routingKey：{}", returned.getRoutingKey());
            log.debug("message：{}", returned.getMessage());
            log.debug("replyCode：{}", returned.getReplyCode());
            log.debug("replyText：{}", returned.getReplyText());
        });
    }
}
