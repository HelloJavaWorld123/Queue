package com.example.rabbitmq.consumer.service;

import com.example.rabbit.common.utils.LoggerUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author : RXK
 * Date : 2020/3/25 18:24
 * Desc:
 */
@Component
public class LogMessageListener extends  AbstractRabbitMqMessage{

	@Override
	@RabbitListener(queuesToDeclare = @Queue(name = "producer-logInfo",durable = "true",declare = "true",autoDelete = "false"))
	void receiveMessage(Message message, Channel channel){
		LoggerUtils.info(message.toString());
		try{
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
		} catch(IOException e){
			try{
				channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
