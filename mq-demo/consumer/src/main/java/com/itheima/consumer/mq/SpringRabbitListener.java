package com.itheima.consumer.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author ZC_Wu 汐
 * @date 2024/12/8 16:30
 * @description 监听队列信息
 * 接收队列中的消息
 */
@Slf4j
@Component // 注册为bean 启动这个项目就会直接注册这个bean接收消息进行监听
public class SpringRabbitListener {

    /**
     * 入门案例
     * @param message
     */
    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        log.info("接收到simple.queue的消息：【{}】", message);
    }


    /**
     * 测试多个消费者的执行顺序
     * 一个队列绑定多个消费者
     * 休眠用于模拟不同服务器的性能不同
     * 发现：默认使用的是轮询投递给消费者消息
     * 修改配置文件：
     *  spring.rabbitmq.listener.simple.prefetch: 1 # 每次只能获取一条消息，处理完成才能获取下一个消息
     * 来实现能者多劳
     * @param message
     */
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message) throws InterruptedException {
        System.out.println("消费者1接收到的消息message：" + message + "," + LocalTime.now());
        Thread.sleep(25);
    }

    /**
     * 测试多个消费者的执行顺序
     * 一个队列绑定多个消费者
     * 休眠用于模拟不同服务器的性能不同
     * @param message
     */
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message) throws InterruptedException {
        System.err.println("消费者2接收到的消息message：" + message + "," + LocalTime.now());
        Thread.sleep(200);
    }

    /**
     * 测试fanout模式交换机
     * 发现该交换机发送到的两个队列都收到了同样的消息
     * @param message
     */
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String message) {
        log.info("消费者1接收到fanout.queue1.queue的消息：【{}】", message);
    }

    /**
     * 测试fanout模式交换机
     * @param message
     */
    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String message) {
        log.info("消费者2接收到fanout.queue2.queue的消息：【{}】", message);
    }

    /**
     * 测试direct模式交换机
     * 发现该交换机发送到的两个队列根据消息的RoutingKey接受了消息
     * @param message
     */
//    @RabbitListener(queues = "direct.queue1")
    // 监听的同时创建指定交换机并绑定队列指定key
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1", durable = "true"),
            exchange = @Exchange(name = "hmall.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}
    ))
    public void listenDirectQueue1(String message) {
        log.info("消费者1接收到direct.queue1的消息：【{}】", message);
    }

    /**
     * 测试direct模式交换机
     * @param message
     */
//    @RabbitListener(queues = "direct.queue2")
    // 监听的同时创建指定交换机并绑定队列指定key
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2", durable = "true"),
            exchange = @Exchange(name = "hmall.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"}
    ))
    public void listenDirectQueue2(String message) {
        log.info("消费者2接收到direct.queue2的消息：【{}】", message);
    }

    /**
     * 测试topic模式交换机
     * 发现该交换机发送到的两个队列根据消息的RoutingKey(可以使用通配符)接受了消息
     * @param message
     */
    @RabbitListener(queues = "topic.queue1")
    public void listenTopicQueue1(String message) {
        log.info("消费者1接收到topic.queue1的消息：【{}】", message);
    }

    /**
     * 测试topic模式交换机
     * @param message
     */
    @RabbitListener(queues = "topic.queue2")
    public void listenTopicQueue2(String message) {
        log.info("消费者2接收到topic.queue2的消息：【{}】", message);
    }

    /**
     * 测试object模式交换机
     * 发送的map类型数据 我们在启动类中指定了json消息转化器，如果不指定会默认使用JDK序列化。
     * JDK序列化存在下列问题：
     *  - 数据体积过大
     *  - 有安全漏洞
     *  - 可读性差
     * @param msg
     */
    @RabbitListener(queues = "object.queue")
    public void listenObjectQueue(Map<String, Object> msg) {
        log.info("消费者1接收到topic.queue2的消息：【{}】", msg);
    }
}
