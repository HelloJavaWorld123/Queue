package com.example.rabbitmq.consumer.service;

import com.rabbitmq.client.AMQP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @author : RXK
 * Date : 2020/3/18 17:50
 * Desc:
 */
@RabbitListener
public abstract class AbstractRabbitMqMessage<T>{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	void receiveMessage(){};
}
