package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.AsyncConsumerRestartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:31
 * Desc:
 */
@Component
public class RabbitConsumerRestartEventListener implements ApplicationListener<AsyncConsumerRestartedEvent>{

	@Override
	public void onApplicationEvent(AsyncConsumerRestartedEvent event){
		LoggerUtils.info("监听到{Consumer Restart Event }事件:{"+event.toString()+"}");
	}
}
