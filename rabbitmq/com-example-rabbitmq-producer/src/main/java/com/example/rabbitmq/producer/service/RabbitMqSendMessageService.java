package com.example.rabbitmq.producer.service;

import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @author : RXK
 * Date : 2020/2/21 17:50
 * Desc:
 */
public interface RabbitMqSendMessageService{

	/**
	 * 发送消息
	 * @param message ： 消息体
	 * 消息体将发送到默认到Exchange 并使用默认的 Routing Key
	 */
	void sendMessage(String message);

	/**
	 * 消息将发送到默认的Exchange 并使用指定的 Routing Key
	 * @param routingKey : 指定的路由键
	 * @param message : 消息体
	 */
	void sendMessage(String routingKey, String message);

	/**
	 * 消息将发送到默认的Exchange 并使用指定的 Routing Key
	 * @param routingKey : 指定的路由键
	 * @param message : 消息体
	 * @param correlationData : 为每个message封装一个唯一个Id,在ConfirmCallBack中可以获取到该唯一的id
	 */
	void sendMessage(String routingKey, String message, CorrelationData correlationData);

	/**
	 * 指定Exchange 和 Routing Key
	 */
	void sendMessage(String exchange,String routingKey,String message);
	void sendMessage(String exchange,String routingKey,String message,CorrelationData correlationData);

	/**
	 * 使用同一个通道 发送消息 直到所有的消息被客户端确认后 才将channel关闭或者返回到链接池中
	 */
	void sendMessageByOneChannel(String exchange, String routingKey, String message, CorrelationData correlationData);

	/**
	 * 在同一个Channel中执行所有的操作，并等待客户端的确认
	 * @return
	 */
	String sendMessageByOneChannelAndCallBack(String exchange, String routingKey, String message, CorrelationData correlationData);


}
