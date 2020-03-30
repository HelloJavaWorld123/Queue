package com.example.rabbitmq.consumer.service;

import com.example.rabbit.common.utils.LoggerUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : RXK
 * Date : 2020/3/25 18:24
 * Desc:
 */
@Component
public class LogMessageListener extends AbstractRabbitMqMessage{

	final AtomicInteger count = new AtomicInteger(0);

	@Override
	@RabbitListener(queuesToDeclare = @Queue(name = "TEST_LOG_QUEUE",
	                                         durable = "true",
	                                         autoDelete = "false"))
	void receiveMessage(Message message, Channel channel){
		LoggerUtils.info(message.toString());
		try{
			if(count.get() == 10){
				count.set(0);
				throw new IllegalArgumentException("进入DeadLetterExchange");
			}
			count.getAndAdd(1);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
		} catch(Exception e){
			try{
				LoggerUtils.error("处理消息时出现异常:",e);
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
