package com.example.rabbitmq.consumer.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

/**
 * @author : RXK
 * Date : 2020/3/18 19:00
 * Desc:
 */
public class CustomerRabbitMqListenerErrorHandler implements RabbitListenerErrorHandler{

	@Override
	public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception{
		return null;
	}
}
