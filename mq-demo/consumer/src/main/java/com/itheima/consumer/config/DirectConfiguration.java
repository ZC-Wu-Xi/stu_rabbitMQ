package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/9 14:59
 * @description 被动创建(如果没有就创建)direct交换机及队列
 * 基于javaBean方式创建 代码太臃肿，太复杂
 */
//@Configuration
public class DirectConfiguration {

    @Bean
    public DirectExchange directExchange() {
        // 创建交换机 两种方式
//        return new DirectExchange("direct.exchange");
        return ExchangeBuilder.directExchange("direct.exchange").build();
    }

    @Bean
    public Queue directQueue1() {
        // 创建队列 两种方式
//        return new Queue("direct.queue1");
        return QueueBuilder.durable("direct.queue1").build();
    }

    @Bean
    public Binding DirectQueue1BindingRed(DirectExchange directExchange, Queue directQueue1) {
        // 绑定队列到交换机指定key 按名字注入
        return BindingBuilder.bind(directQueue1).to(directExchange).with("red");
    }

    @Bean
    public Binding DirectQueue1BindingBlue(DirectExchange directExchange, Queue directQueue1) {
        // 绑定队列到交换机指定key 按名字注入
        return BindingBuilder.bind(directQueue1).to(directExchange).with("blue");
    }

    @Bean
    public Queue directQueue2() {
        // 创建队列 两种方式
//        return new Queue("direct.queue1");
        return QueueBuilder.durable("direct.queue2").build();
    }

    @Bean
    public Binding DirectQueue2BindingRed(DirectExchange directExchange, Queue directQueue2) {
        // 绑定队列到交换机指定key 按名字注入
        return BindingBuilder.bind(directQueue2).to(directExchange).with("red");
    }

    @Bean
    public Binding DirectQueue2BindingYellow(DirectExchange directExchange, Queue directQueue2) {
        // 绑定队列到交换机指定key 按名字注入
        return BindingBuilder.bind(directQueue2).to(directExchange).with("yellow");
    }
}
