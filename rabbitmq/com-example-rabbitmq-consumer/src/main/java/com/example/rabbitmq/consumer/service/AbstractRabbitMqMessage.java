package com.example.rabbitmq.consumer.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

/**
 * @author : RXK
 * Date : 2020/3/18 17:50
 * Desc:
 */
public abstract class AbstractRabbitMqMessage{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	void receiveMessage(Message message,Channel channel){};
}
