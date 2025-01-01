package com.itheima.consumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC_Wu 汐
 * @date 2025/1/1 16:22
 * @description 消费者可靠性——消费者失败处理策略之`RepublishMessageRecoverer`：本地重试耗尽后，将失败消息投递到指定的交换机
 */
@Configuration
// @ConditionalOnProperty条件化的 Bean 加载,当name这个属性的值为true时加载此bean,在 application.yaml中配置了
// 当然也可以不加该注解，为了严谨，加上比较好
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.retry.enabled", havingValue = "true")
public class ErrorMessageConfiguration {

    // 定义交换机
    @Bean
    public DirectExchange errorExchange() {
        return new DirectExchange("error.direct");
    }

    // 定义队列
    @Bean
    public Queue errorQueue() {
        return new Queue("error.queue");
    }

    // 绑定关系
    @Bean
    public Binding errorQueueBinding(Queue errorQueue, DirectExchange errorExchange) {
        return BindingBuilder.bind(errorQueue).to(errorExchange).with("error");
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
