package com.example.rabbitmq.producer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.core.BrokerEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author : RXK
 * Date : 2020/3/24 18:56
 * Desc: 监听 Broker的各种的Event
 *
 * RabbitMq的事件监听需要打开 event-exchange-plugin 否则启动不起来
 *
 */
public class CustomerBrokerEventListener implements ApplicationListener<BrokerEvent>{

	@Override
	public void onApplicationEvent(BrokerEvent event){
		LoggerUtils.info("接收到的Event的是：{"+event.toString()+"}");
	}
}
