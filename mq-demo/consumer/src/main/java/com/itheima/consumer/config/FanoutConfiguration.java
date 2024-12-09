package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/9 14:59
 * @description 被动创建(如果没有就创建)fanout交换机及队列
 * 基于javaBean方式创建 代码太臃肿，太复杂
 */
@Configuration
public class FanoutConfiguration {

    @Bean
    public FanoutExchange fanoutExchange() {
        // 创建交换机 两种方式
//        return new FanoutExchange("fanout.exchange");
        return ExchangeBuilder.fanoutExchange("fanout.exchange").build();
    }

    @Bean
    public Queue fanoutQueue1() {
        // 创建队列 两种方式
//        return new Queue("fanout.queue1");
        return QueueBuilder.durable("fanout.queue1").build();
    }

    @Bean
    public Binding FanoutQueue1Binding(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
        // 绑定队列到交换机 按名字注入
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    @Bean
    public Queue fanoutQueue2() {
        // 创建队列 两种方式
//        return new Queue("fanout.queue1");
        return QueueBuilder.durable("fanout.queue2").build();
    }

    @Bean
    public Binding FanoutQueue2Binding(FanoutExchange fanoutExchange, Queue fanoutQueue2) {
        // 绑定队列到交换机 按名字注入
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}
