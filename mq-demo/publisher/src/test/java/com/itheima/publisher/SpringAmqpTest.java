package com.itheima.publisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/8 16:23
 * @description 消息发送
 */
@Slf4j
@SpringBootTest
class SpringAmqpTest {

    // SpringAMQP提供了`RabbitTemplate`工具类，方便我们发送消息
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到simple.queue队列
     */
    @Test
    public void testSimpleQueue() {
        // 1. 定义队列名称
        String queueName = "simple.queue";
        // 2. 定义消息
        String message = "Hello, Spring AMQP!";
        // 3. 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }


    /**
     * 发送50条消息到work.queue队列
     * 观察consumer微服务的日志发现消息被两个消费者轮询处理
     * 修改配置文件测试后实现了能者多劳
     */
    @Test
    public void testWorkQueue() {
        // 1. 定义队列名称
        String queueName = "work.queue";

        for (int i = 1; i <= 50; i++) {
            // 2. 定义消息
            String message = "Hello, Spring AMQP_";
            // 3. 发送消息
            rabbitTemplate.convertAndSend(queueName, message + i);
        }
    }

    /**
     * 发送消息到hmall.fanout交换机
     * 发现该交换机绑定的两个队列都收到了消息
     */
    @Test
    public void testFanoutQueue() {
        // 1. 定义交换机名称
        String exchangeName = "hmall.fanout";
        // 2. 定义消息
        String message = "Hello, everyone!";
        // 3. 发送消息，参数分别是：交互机名称、RoutingKey（暂时为空）、消息
        rabbitTemplate.convertAndSend(exchangeName, null, message);
    }

    /**
     * 发送消息到hmall.direct交换机
     * 发现该交换机绑定的两个队列都收到了消息
     */
    @Test
    public void testDirectQueue() {
        // 1. 定义交换机名称
        String exchangeName = "hmall.direct";
        // 2. 定义消息
        String redMessage = "红色：震惊，大学男宿舍后面竟然发现女尸！";
        String blueMessage = "蓝色：通知：女尸是充气的！";
        String yellowMessage = "黄色：警惕：一名男子把女尸抱回了宿舍！";
        // 3. 发送消息，参数分别是：交互机名称、RoutingKey、消息
        rabbitTemplate.convertAndSend(exchangeName, "red", redMessage);
        rabbitTemplate.convertAndSend(exchangeName, "blue", blueMessage);
        rabbitTemplate.convertAndSend(exchangeName, "yellow", yellowMessage);
    }

    /**
     * 发送消息到hmall.topic交换机
     * 发现该交换机绑定的两个队列都收到了消息
     */
    @Test
    public void testTopicQueue() {
        // 1. 定义交换机名称
        String exchangeName = "hmall.topic";
        // 2. 定义消息
        String message = "红色：震惊，大学男宿舍后面竟然发现女尸！";
        String message2 = "天气：今天天气晴！";
        // 3. 发送消息，参数分别是：交互机名称、RoutingKey、消息
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
        rabbitTemplate.convertAndSend(exchangeName, "china.weather", message2);
    }

    @Test
    public void testSendObject() {
        // 1. 定义队列名称
        String queueName = "object.queue";
        // 2. 定义消息
        Map<String, Object> msg = new HashMap<>(2);
        msg.put("name", "Jack");
        msg.put("age", 21);
        // 3. 发送消息，参数分别是：交互机名称、RoutingKey、消息
        rabbitTemplate.convertAndSend(queueName, msg);
    }

    /**
     * 测试生产者确认机制
     * ReturnCallback 和 ConfirmCallback
     */
    @Test
    public void testConfirmCallback() {
        // 1. 创建CorrelationData
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());

        // 2.给Future添加ConfirmCallback
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                // 2.1.Future发生异常时的处理逻辑，基本不会触发
                log.error("spring amqp 处理结果异常", ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
            // 2.2.Future接收到回执的处理逻辑，参数中的result就是回执内容
                if(result.isAck()){ // result.isAck()，boolean类型，true代表ack回执，false 代表 nack回执
                    log.debug("发送消息成功，收到 ConfirmCallback ack!");
                }else{ // result.getReason()，String类型，返回nack时的异常描述
                    log.error("发送消息失败，收到 ConfirmCallback nack, reason : {}", result.getReason());
                }
            }
        });
        // 3.发送消息
        // 3.1 定义交换机名称
        String exchangeName = "hmall.direct";
//        String exchangeName = "hmall.dir1ect";
        // 3.2 定义消息
        String redMessage = "红色：震惊，大学男宿舍后面竟然发现女尸！";
        // 3.3 发送消息，参数分别是：交互机名称、RoutingKey、消息
        rabbitTemplate.convertAndSend(exchangeName, "red", redMessage, cd);
    }

    /**
     * 测试持久化消息和非持久化消息的性能
     * 持久化消息不会发生阻塞，并且速度较快
     */
    @Test
    void testSendMessage() {
        // 1. 自定义构建消息
        Message message = MessageBuilder.withBody("hello, SpringAMQP".getBytes(StandardCharsets.UTF_8))
//                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT) // 将消息设置为非持久的
                // SpringAMQP发出的消息默认都是持久化的
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT) // 将消息设置为持久的
                .build();

        for (int i = 0; i < 1000000; i++) {
            rabbitTemplate.convertAndSend("simple.queue", message);
//            rabbitTemplate.convertAndSend("lazy.queue", message); // lazyqueue的这种模式并发更好，而且不走内存，直接写到磁盘
        }
    }
}