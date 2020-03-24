package com.example.rabbitmq.consumer.config.annon;

import org.springframework.amqp.rabbit.annotation.Queue;

/**
 * @author : RXK
 * Date : 2020/3/24 15:34
 * Desc:
 * TODO 一个Exchange 绑定多个Queue
 */
@CustomerRabbitListener(@Queue(value = "TEST_QUEUE_TWO", declare = "true", durable = "true", exclusive = "false", autoDelete = "true"))
@CustomerRabbitListener(@Queue(value = "TEST_QUEUE_ONE",declare = "true",durable = "true",exclusive = "false",autoDelete = "true"))
public class CustomerRabbitListenerTest{
}
