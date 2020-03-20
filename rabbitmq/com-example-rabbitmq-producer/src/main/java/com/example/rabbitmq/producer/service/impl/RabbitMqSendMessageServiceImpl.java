package com.example.rabbitmq.producer.service.impl;

import com.example.rabbitmq.producer.service.DefaultConfirmCallBackService;
import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/2/21 18:48
 * Desc: 发送消息的具体实现
 */
@Service
public class RabbitMqSendMessageServiceImpl implements RabbitMqSendMessageService{


	private final RabbitTemplate rabbitTemplate;

	private final DefaultConfirmCallBackService defaultConfirmCallBackService;

	public RabbitMqSendMessageServiceImpl(RabbitTemplate rabbitTemplate, DefaultConfirmCallBackService defaultConfirmCallBackService){
		this.rabbitTemplate = rabbitTemplate;
		this.defaultConfirmCallBackService = defaultConfirmCallBackService;
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

	@Override
	public void sendMessage(String routingKey, String message, CorrelationData correlationData){
		Assert.isTrue(!StringUtils.isAllEmpty(routingKey,message)|| Objects.nonNull(correlationData),"参数不能为空");
		rabbitTemplate.convertAndSend(routingKey, (Object) message,correlationData);
	}

	@Override
	public void sendMessage(String exchange, String routingKey, String message){
		Assert.isTrue(StringUtils.isAllEmpty(exchange,routingKey,message),"发送消息的参数不能为空");
		rabbitTemplate.convertAndSend(exchange,routingKey,message);
	}

	@Override
	public void sendMessage(String exchange, String routingKey, String message, CorrelationData correlationData){
		Assert.isTrue(!StringUtils.isAllEmpty(exchange,routingKey,message) || Objects.nonNull(correlationData),"发送消息的参数不能为空");
		rabbitTemplate.setConfirmCallback(defaultConfirmCallBackService);
		rabbitTemplate.convertAndSend(exchange,routingKey,message,correlationData);
	}
}
