package com.example.rabbitmq.consumer.config.event;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.ConsumeOkEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/26 11:40
 * Desc: 消费者成功连接到 Broker 时发布的事件
 */
@Component
public class RabbitConsumeOkEventListener implements ApplicationListener<ConsumeOkEvent>{
	@Override
	public void onApplicationEvent(ConsumeOkEvent event){
		LoggerUtils.info("监听到:{Consumer Ok Event } 事件 {"+event.toString()+"}");
	}
}
