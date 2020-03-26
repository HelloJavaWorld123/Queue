package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.AsyncConsumerStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:33
 * Desc:
 */
@Component
public class RabbitConsumerStartedEventListener implements ApplicationListener<AsyncConsumerStartedEvent>{

	@Override
	public void onApplicationEvent(AsyncConsumerStartedEvent event){
		LoggerUtils.info("监听到:{Consumer Started Event } 事件 {"+event.toString()+"}");
	}
}
