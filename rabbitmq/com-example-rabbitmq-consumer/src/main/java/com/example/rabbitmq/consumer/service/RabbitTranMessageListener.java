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
 * Date : 2020/4/1 18:17
 * Desc:
 */
@Service
public class RabbitTranMessageListener extends AbstractRabbitMqMessage{

	@Override
	@RabbitListener(queuesToDeclare ={
			@Queue(name = "TEST_TRANSACTION_QUEUE",durable = "true",autoDelete = "false",declare = "true")
	},containerFactory = "transactionSimpleRabbitListenerContainerFactory")
	void receiveMessage(Message message, Channel channel){
		try{
			LoggerUtils.info("接收到事务消息{"+message.getMessageProperties().getDeliveryTag()+"}");
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}catch(Exception e){
			try{
				channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
			} catch(IOException ex){
				LoggerUtils.error("处理事务消息出现异常：",ex);
			}
		}
	}
}
