package com.example.rabbitmq.consumer.config.annon;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.annotation.AliasFor;
import sun.awt.SunHints;

import java.lang.annotation.*;

/**
 * @author : RXK
 * Date : 2020/3/24 14:47
 * Desc:
 * 当在通过一个Exchange上,有多个Queue时，可以定义自己的元注解，减少不必要的配置
 */
@RabbitListener(bindings = {@QueueBinding(
		//使用默认的队列
		value = @Queue,
		exchange = @Exchange(name = "Test_exchange", type = ExchangeTypes.FANOUT,durable = "true",autoDelete = "false",declare = "true"))
},containerFactory = "rabbitListenerContainerFactory")
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = CustomerRabbitListeners.class)
public @interface CustomerRabbitListener{

	@AliasFor(annotation = QueueBinding.class,attribute = "value")
	Queue value();
}

