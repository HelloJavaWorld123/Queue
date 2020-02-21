package com.example.rabbitmq.producer.config;

import com.example.rabbitmq.producer.enums.RabbitMqEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.BackgroundPreinitializer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : RXK
 * Date : 2020/2/21 14:15
 * Desc:
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitMqConfig{

	@Autowired
	private RabbitProperties properties;

	@Autowired
	private CustomerChannelListener customerChannelListener;

	@Autowired
	private CustomerConnectionListener customerConnectionListener;

	@Bean(initMethod = "start",destroyMethod = "destroy")
	@ConditionalOnMissingBean(value = RabbitTemplate.class)
	public RabbitTemplate rabbitTemplate(CachingConnectionFactory cachingConnectionFactory){
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setEncoding("UTF-8");
		rabbitTemplate.setConnectionFactory(cachingConnectionFactory);
		rabbitTemplate.afterPropertiesSet();
		return rabbitTemplate;
	}


	@Bean(destroyMethod = "destroy")
	@ConditionalOnMissingBean(value = CachingConnectionFactory.class)
	public CachingConnectionFactory cachingConnectionFactory(){
		CachingConnectionFactory factory = new CachingConnectionFactory();

		factory.setHost(properties.getHost());
		factory.setPort(properties.getPort());
		factory.setUsername(properties.getUsername());
		factory.setPassword(properties.getPassword());
		factory.setVirtualHost(properties.getVirtualHost());
		factory.setPublisherReturns(properties.isPublisherReturns());
		factory.setPublisherConfirmType(properties.getPublisherConfirmType());
		factory.setCacheMode(properties.getCache().getConnection().getMode());
		factory.setChannelCacheSize(properties.getCache().getChannel().getSize());
		factory.setConnectionCacheSize(properties.getCache().getConnection().getSize());

		//添加自定义的channel监听器
		factory.addChannelListener(customerChannelListener);
		//添加自定义的connection的监听器
		factory.addConnectionListener(customerConnectionListener);

		factory.afterPropertiesSet();
		return factory;
	}


	@Bean
	public DirectExchange directExchange(){
		return new DirectExchange(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(),Boolean.TRUE,Boolean.FALSE);
	}

	@Bean
	public Queue queue(){
		return new Queue(RabbitMqEnum.QueueEnum.TEST_QUEUE.name(),Boolean.TRUE,Boolean.FALSE,Boolean.FALSE);
	}

	@Bean
	public Binding binding(){
		return BindingBuilder.bind(queue()).to(directExchange()).with(RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name());
	}




}
