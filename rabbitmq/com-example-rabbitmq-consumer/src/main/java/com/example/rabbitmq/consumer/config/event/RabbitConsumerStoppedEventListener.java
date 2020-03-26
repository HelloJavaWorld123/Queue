package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.AsyncConsumerStoppedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:34
 * Desc:
 */
@Component
public class RabbitConsumerStoppedEventListener implements ApplicationListener<AsyncConsumerStoppedEvent>{
	@Override
	public void onApplicationEvent(AsyncConsumerStoppedEvent event){
		LoggerUtils.info("监听到:{Consumer Stopped Event } 事件 {"+event.toString()+"}");
	}
}
