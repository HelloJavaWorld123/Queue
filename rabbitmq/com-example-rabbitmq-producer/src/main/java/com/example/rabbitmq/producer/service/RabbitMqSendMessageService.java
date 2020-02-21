package com.example.rabbitmq.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author : RXK
 * Date : 2020/2/21 17:50
 * Desc:
 */
public interface RabbitMqSendMessageService extends RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{

	 Logger LOGGER = LoggerFactory.getLogger(RabbitMqSendMessageService.class);

	/**
	 * 发送消息
	 * @param message ： 消息体
	 */
	void sendMessage(String message);

	void sendMessage(String routingKey, String message);

	/**
	 * 确认消息的回调
	 * @param correlationData ： 消息id  以及确认信息
	 * @param ack ： 是否确认
	 * @param cause ：
	 */
	@Override
	default void confirm(CorrelationData correlationData, boolean ack, String cause){
		LOGGER.info("返回的确认的消息体id是:{},确认消息内容是:{},是否已经确认:{},cause:{}",correlationData.toString(),correlationData.getReturnedMessage(),ack,cause);
	}

	/**
	 * 返回消息回调
	 * @param message ： 消息体
	 * @param replyCode ： 回复码
	 * @param replyText ： 回复内容
	 * @param exchange ： 交换机
	 * @param routingKey ： 路由键
	 */
	@Override
	default void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey){
		LOGGER.info("消息体是:{},回复编码是:{},回复的内容是:{},交换机是:{},路由key是:{}",message.toString(),replyCode,replyText,exchange,routingKey);
	}
}
