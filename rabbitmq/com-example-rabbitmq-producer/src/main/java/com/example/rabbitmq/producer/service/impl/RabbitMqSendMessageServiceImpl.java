package com.example.rabbitmq.producer.service.impl;

import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author : RXK
 * Date : 2020/2/21 18:48
 * Desc: 发送消息的具体实现
 */
@Service
public class RabbitMqSendMessageServiceImpl implements RabbitMqSendMessageService{


	private final RabbitTemplate rabbitTemplate;

	public RabbitMqSendMessageServiceImpl(RabbitTemplate rabbitTemplate){
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void sendMessage(String message){
		Assert.hasText(message,"消息体不能为空");
		rabbitTemplate.convertAndSend(message);
	}

	@Override
	public void sendMessage(String routingKey, String message){
		Assert.isTrue(StringUtils.isNotEmpty(routingKey)&&StringUtils.isNotEmpty(message),"routingKey或者message不能为空");
		rabbitTemplate.convertAndSend(routingKey,message);
	}
}