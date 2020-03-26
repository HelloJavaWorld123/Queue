package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.core.BrokerEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:42
 * Desc:
 */
@Component
public class RabbitBrokerEventListener implements ApplicationListener<BrokerEvent>{
	@Override
	public void onApplicationEvent(BrokerEvent event){
		LoggerUtils.info("监听到:{Broker Event } 事件 {"+event.toString()+"}");
	}
}
