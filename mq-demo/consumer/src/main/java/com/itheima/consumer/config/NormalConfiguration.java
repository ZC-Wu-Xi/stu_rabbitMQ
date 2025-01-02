package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC_Wu 汐
 * @date 2025/1/2 16:47
 * @description 交换机和队列
 * 模拟演示死信交换机
 */
@Configuration
public class NormalConfiguration {
    @Bean
    public DirectExchange normalExchange() {
        // 创建交换机 两种方式
//        return new DirectExchange("direct.exchange");
        return ExchangeBuilder.directExchange("normal.direct").build();
    }

    /**
     * 定义消息队列，与死信交换机绑定关系
     * 当消息成为死信(如消息过期未被消费)，该消息就会发给死信交换机巧妙地实现延迟消息
     * @return
     */
    @Bean
    public Queue normalQueue() {
        return QueueBuilder
                .durable("normal.queue")
                .deadLetterExchange("dlx.direct")
                .build();
    }

    @Bean
    public Binding normalExchangeBinding(DirectExchange normalExchange, Queue normalQueue) {
        // 绑定队列到交换机指定key 按名字注入
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("hi");
    }
}
