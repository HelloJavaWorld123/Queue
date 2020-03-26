package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.ListenerContainerIdleEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:36
 * Desc:
 */
@Component
public class RabbitContainerIdleEventListener implements ApplicationListener<ListenerContainerIdleEvent>{
	@Override
	public void onApplicationEvent(ListenerContainerIdleEvent event){
		LoggerUtils.info("监听到:{Listener Container Idle Event } 事件 {"+event.toString()+"}");
	}
}
