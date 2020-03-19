package com.example.rabbitmq.producer.config;

import com.example.rabbitmq.producer.service.RabbitMqSendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/19 18:52
 * Desc:
 */
@Component
public class ConsumerReturnCallBack implements RabbitTemplate.ReturnCallback{

	static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSendMessageService.class);

	/**
	 * 返回消息回调
	 * @param message ： 消息体
	 * @param replyCode ： 回复码
	 * @param replyText ： 回复内容
	 * @param exchange ： 交换机
	 * @param routingKey ： 路由键
	 */
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey){
		LOGGER.info("消息体是:{},回复编码是:{},回复的内容是:{},交换机是:{},路由key是:{}",message.toString(),replyCode,replyText,exchange,routingKey);
	}
}
