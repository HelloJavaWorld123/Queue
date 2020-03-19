package com.example.rabbitmq.producer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/19 18:49
 * Desc:
 */
@Component
public class BrokerConfirmCallBack implements RabbitTemplate.ConfirmCallback{

	Logger LOGGER = LoggerFactory.getLogger(BrokerConfirmCallBack.class);

	/**
	 * 确认消息的回调
	 * @param correlationData ： 消息id  以及确认信息
	 * @param ack ： 是否确认
	 * @param cause ：
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause){
		LOGGER.info("返回的确认的消息体id是:{},确认消息内容是:{},是否已经确认:{},cause:{}",correlationData.toString(),correlationData.getReturnedMessage(),ack,cause);
	}
}
