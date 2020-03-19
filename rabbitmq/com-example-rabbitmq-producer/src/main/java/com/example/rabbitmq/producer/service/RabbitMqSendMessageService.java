package com.example.rabbitmq.producer.service;

/**
 * @author : RXK
 * Date : 2020/2/21 17:50
 * Desc:
 */
public interface RabbitMqSendMessageService{

	/**
	 * 发送消息
	 * @param message ： 消息体
	 */
	void sendMessage(String message);

	void sendMessage(String routingKey, String message);

}
