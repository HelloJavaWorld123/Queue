package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.connection.ConnectionUnblockedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:39
 * Desc:
 */
@Component
public class RabbitUnBlockedEventListener implements ApplicationListener<ConnectionUnblockedEvent>{
	@Override
	public void onApplicationEvent(ConnectionUnblockedEvent event){
		LoggerUtils.info("监听到:{Connection UnBlocked Event } 事件 {"+event.toString()+"}");
	}
}
