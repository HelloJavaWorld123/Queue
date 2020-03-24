package com.example.rabbitmq.consumer.config.rabbit;

import com.example.rabbit.common.utils.LoggerUtils;
import org.springframework.amqp.rabbit.listener.ListenerContainerConsumerFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/24 13:57
 * Desc: 监听 Rabbit Consumer  Faild 或者 Idle Container 时推送的事件
 * 其他不同阶段的Event:
 * AsyncConsumerStartedEvent ：启动使用者时.
 *
 * AsyncConsumerRestartedEvent ：在失败后重新启动使用者时-仅SimpleMessageListenerContainer .
 *
 * AsyncConsumerTerminatedEvent ：使用者正常停止时.
 *
 * AsyncConsumerStoppedEvent ：停止使用者时-仅SimpleMessageListenerContainer .
 *
 * ConsumeOkEvent ：当consumeOk从经纪人收到包含队列名称和consumerTag
 *
 * ListenerContainerIdleEvent: 检测空闲的异步使用者 .
 */
@Component
public class RabbitApplicationListener implements ApplicationListener<ListenerContainerConsumerFailedEvent>{
	@Override
	public void onApplicationEvent(ListenerContainerConsumerFailedEvent event){
		LoggerUtils.info("---------------接收到 ConsumerFailedEvent事件:{"+event.toString()+"}---------------------");
	}
}
