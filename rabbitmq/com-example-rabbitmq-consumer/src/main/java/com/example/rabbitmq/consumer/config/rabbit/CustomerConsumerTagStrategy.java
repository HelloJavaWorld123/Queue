package com.example.rabbitmq.consumer.config.rabbit;

import com.example.rabbit.common.utils.IPAddressUtils;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/25 16:40
 * Desc: 在消费者监听的队列中，当前消费者的标识
 */
@Component
public class CustomerConsumerTagStrategy implements ConsumerTagStrategy{

	@Override
	public String createConsumerTag(String queue){
		return IPAddressUtils.ipAndHostName()+":"+queue;
	}
}
