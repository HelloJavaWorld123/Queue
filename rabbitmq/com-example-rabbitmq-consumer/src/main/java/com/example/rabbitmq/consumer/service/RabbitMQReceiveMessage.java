package com.example.rabbitmq.consumer.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author : RXK
 * Date : 2020/3/18 18:52
 * Desc:
 */
@Service
public class RabbitMQReceiveMessage extends AbstractRabbitMqMessage{

	@Override
	@RabbitListener(queuesToDeclare = {
			@Queue(name = "TEST_QUEUE",durable = "true",exclusive = "false",autoDelete = "false",ignoreDeclarationExceptions = "false")
	})
	void receiveMessage(Message message,Channel channel){
		String s = message.toString();
		String body = new String(message.getBody());
		logger.info("--------消息是:{}------",s);
		logger.info("--------消息体是:{}------",body);
		try{
			channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,false);
		} catch(Exception e){
			logger.error("---Consumer---消费出现异常:-----",e);
			try{
				channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
				channel.basicRecover(true);
			} catch(IOException ex){
				logger.error("---Consumer---消费出现异常后,重新入队出现异常:-----",e);
			}
		}
	}
}
