package com.example.rabbitmq.producer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/2/21 15:09
 * Desc:
 */
//@Component
public class MessageRabbitTemplate extends RabbitTemplate{

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageRabbitTemplate.class);

	@Override
	protected void doStart(){
		LOGGER.info("RabbitMQ 启动,RabbitTemplate 配置完成");
	}

	@Override
	protected void doStop(){
		super.doStop();
		LOGGER.info("RabbitMQ 停止,RabbitTemplate 卸载完成");
	}
}
