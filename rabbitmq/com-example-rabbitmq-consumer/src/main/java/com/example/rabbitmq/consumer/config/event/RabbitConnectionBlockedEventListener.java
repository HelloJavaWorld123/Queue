package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.connection.ConnectionBlockedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:38
 * Desc:
 */
@Component
public class RabbitConnectionBlockedEventListener implements ApplicationListener<ConnectionBlockedEvent>{

	@Override
	public void onApplicationEvent(ConnectionBlockedEvent event){
		LoggerUtils.info("监听到:{Connection Blocked Event } 事件 {"+event.toString()+"}");
	}
}
