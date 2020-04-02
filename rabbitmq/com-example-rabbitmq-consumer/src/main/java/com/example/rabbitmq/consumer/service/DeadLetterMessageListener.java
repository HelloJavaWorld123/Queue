package com.example.rabbitmq.consumer.service;

import com.example.rabbit.common.utils.LoggerUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author : RXK
 * Date : 2020/4/2 13:28
 * Desc:
 */
@Service
public class DeadLetterMessageListener extends AbstractRabbitMqMessage{

	@Override
	@RabbitListener(queuesToDeclare = {@Queue(name = "TEST_X_DEAD_LETTER_EXCHANGE_ARG",
	                                          autoDelete = "false",
	                                          durable = "true")})
	void receiveMessage(Message message, Channel channel){
		try{
			LoggerUtils.info("Dead Letter 接收到消息：{" + message.toString() + "}");
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch(IOException e){
			LoggerUtils.error("处理DEAd Letter Message 出现异常:",e);
			try{
				channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
