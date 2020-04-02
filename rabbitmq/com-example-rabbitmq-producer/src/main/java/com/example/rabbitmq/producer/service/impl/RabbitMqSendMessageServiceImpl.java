package com.example.rabbitmq.producer.service.impl;

import com.example.rabbit.common.enums.RabbitMqEnum;
import com.example.rabbit.common.utils.LoggerUtils;
import com.example.rabbitmq.producer.service.DefaultConfirmCallBackService;
import com.example.rabbitmq.producer.service.DefaultReturnCallBackService;
import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
import com.rabbitmq.client.ConfirmCallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/2/21 18:48
 * Desc: 发送消息的具体实现
 */
@Service
public class RabbitMqSendMessageServiceImpl implements RabbitMqSendMessageService{


	private final RabbitTemplate rabbitTemplate;

	private final RabbitTemplate transactionalRabbitTemplate;

	private final DefaultReturnCallBackService defaultReturnCallBackService;

	private final DefaultConfirmCallBackService defaultConfirmCallBackService;

	public RabbitMqSendMessageServiceImpl(RabbitTemplate rabbitTemplate, RabbitTemplate transactionalRabbitTemplate, DefaultReturnCallBackService defaultReturnCallBackService, DefaultConfirmCallBackService defaultConfirmCallBackService){
		this.rabbitTemplate = rabbitTemplate;
		this.transactionalRabbitTemplate = transactionalRabbitTemplate;
		this.defaultReturnCallBackService = defaultReturnCallBackService;
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
		rabbitTemplate.setReturnCallback(defaultReturnCallBackService);
		Message message1 = MessageBuilder.withBody(message.getBytes()).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
		rabbitTemplate.convertAndSend(exchange,routingKey,message1,correlationData);
	}

	/**
	 * 在同一个Channel中发送所有的消息
	 */
	@Override
	public void sendMessageByOneChannel(String exchange,String routingKey,String message,CorrelationData correlationData){
		rabbitTemplate.invoke(operations -> operations.convertSendAndReceive(exchange,routingKey,message,correlationData));
	}

	@Override
	public String sendMessageByOneChannelAndCallBack(String exchange, String routingKey, String message, CorrelationData correlationData){
		StringBuilder callBackConfirm = new StringBuilder();
		rabbitTemplate.invoke(operations -> {
			operations.convertSendAndReceive(exchange, routingKey, message, correlationData);
			boolean b = operations.waitForConfirms(1000);
			return true;
		},(tag,multi)->callBackConfirm.append("ack-callback:").append(tag).append(multi)
				,(tag,multi)->callBackConfirm.append("nack-callback:").append(tag).append(multi));
		return callBackConfirm.toString();
	}

	@Override
	@Transactional(rollbackFor = Exception.class,transactionManager = "rabbitTransactionManager")
	public void sendTransactionMessage(CorrelationData correlationData, String messageBody){
		Message message = MessageBuilder.withBody(messageBody.getBytes()).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
		transactionalRabbitTemplate.setConfirmCallback(defaultConfirmCallBackService);
		transactionalRabbitTemplate.setReturnCallback(defaultReturnCallBackService);
		transactionalRabbitTemplate.convertAndSend(RabbitMqEnum.ExchangeEnum.TEST_TRANSACTION_DIRECT_EXCHANGE.name(),RabbitMqEnum.RoutingKey.TEST_TRANSACTION_KEY.name(),message,correlationData);

		int a = 1 / 0;
		//第二个事务发生异常
		transactionalRabbitTemplate.convertAndSend(
				RabbitMqEnum.ExchangeEnum.TEST_DEAD_LETTER_EXCHANGE.name(),
				RabbitMqEnum.RoutingKey.TEST_DEAD_LETTER_EXCHANGE_KEY.name(),
				message,
				correlationData
		);
	}

	@Override
	public void sendWaitConfirmCallBack(String message, CorrelationData correlationData){
		Message messageInfo = MessageBuilder.withBody(message.getBytes()).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
		rabbitTemplate.setConfirmCallback(defaultConfirmCallBackService);
		rabbitTemplate.setReturnCallback(defaultReturnCallBackService);
		rabbitTemplate.invoke(options -> {
			options.convertSendAndReceive(RabbitMqEnum.ExchangeEnum.TEST_FANOUT_EXCHANGE.name(), RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name(), messageInfo, correlationData);
			boolean waitForConfirms = options.waitForConfirms(10);
			LoggerUtils.info("等待确认的结果是：wait For Confirms Result: {" + waitForConfirms + "}");
			return waitForConfirms;
		});
	}

	@Override
	public void sendDelayMessage(String message, CorrelationData correlationData){
		Message messageInfo = MessageBuilder
				.withBody(message.getBytes())
				.setDeliveryMode(MessageDeliveryMode.PERSISTENT)
				.setContentType("application/json")
				.setContentEncoding(StandardCharsets.UTF_8.name())
				.setHeader("x-delay","300000")
				.build();

		rabbitTemplate.setConfirmCallback(defaultConfirmCallBackService);
		rabbitTemplate.setReturnCallback(defaultReturnCallBackService);

		rabbitTemplate.convertAndSend(RabbitMqEnum.ExchangeEnum.TEST_DELAY_EXCHANGE.name(),RabbitMqEnum.RoutingKey.TEST_DELAY_ROUTING_KEY.name(),messageInfo,correlationData);

	}

}
