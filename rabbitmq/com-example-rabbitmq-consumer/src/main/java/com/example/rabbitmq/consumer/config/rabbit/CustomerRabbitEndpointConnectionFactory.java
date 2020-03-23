package com.example.rabbitmq.consumer.config.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;

/**
 * @author : RXK
 * Date : 2020/3/23 15:07
 * Desc: 为 Endpoint 自定义 RabbitListenerConenctionFactory
 */
public class CustomerRabbitEndpointConnectionFactory implements RabbitListenerConfigurer{


//	@Autowired
	private SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory;

	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar){
//		SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
//		registrar.registerEndpoint(endpoint,simpleRabbitListenerContainerFactory);
//		registrar.afterPropertiesSet();
	}
}
