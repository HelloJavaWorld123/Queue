package com.example.rabbitmq.consumer.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;

/**
 * @author : RXK
 * Date : 2020/3/11 18:16
 * Desc:
 */
@EnableRabbit
@Configuration
@EnableConfigurationProperties(value = {RabbitProperties.class})
public class RabbitMqConfig{

	@Autowired
	private RabbitProperties rabbitProperties;

	@Autowired
	private RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler;


	@Bean(destroyMethod = "destroy")
	@ConditionalOnMissingBean(org.springframework.amqp.rabbit.connection.ConnectionFactory.class)
	public CachingConnectionFactory factory(ConnectionFactory connectionFactory){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
		PropertyMapper mapper = PropertyMapper.get();
		RabbitProperties.Cache.Channel channel = rabbitProperties.getCache().getChannel();
		mapper.from(channel::getSize).to(cachingConnectionFactory::setChannelCacheSize);
		RabbitProperties.Cache.Connection connection = rabbitProperties.getCache().getConnection();
		mapper.from(connection::getMode).to(cachingConnectionFactory::setCacheMode);
		mapper.from(connection::getSize).to(cachingConnectionFactory::setConnectionCacheSize);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}


	@Bean
	@ConditionalOnMissingBean(com.rabbitmq.client.ConnectionFactory.class)
	public ConnectionFactory connectionFactory(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		PropertyMapper propertyMapper = PropertyMapper.get();
		propertyMapper.from(rabbitProperties::getPort).to(connectionFactory::setPort);
		propertyMapper.from(rabbitProperties::getHost).to(connectionFactory::setHost);
		propertyMapper.from(rabbitProperties::getPassword).to(connectionFactory::setPassword);
		propertyMapper.from(rabbitProperties::getUsername).to(connectionFactory::setUsername);
		propertyMapper.from(rabbitProperties::getVirtualHost).to(connectionFactory::setVirtualHost);
		propertyMapper.from(rabbitProperties::getRequestedHeartbeat)
				.whenNonNull()
				.asInt(Duration::getSeconds)
				.to(connectionFactory::setRequestedHeartbeat);
		propertyMapper.from(rabbitProperties::getConnectionTimeout).whenNonNull().asInt(Duration::getSeconds)
				.to(connectionFactory::setConnectionTimeout);
		connectionFactory.setAutomaticRecoveryEnabled(Boolean.TRUE);
		connectionFactory.setExceptionHandler(rabbitConnectionExceptionHandler);
		return connectionFactory;
	}


	@Bean
	public RetryTemplate retryTemplate(){
		return new RetryTemplate();
	}

	@Bean()
	@ConditionalOnMissingBean(RabbitTemplate.class)
	public RabbitTemplate rabbitTemplate(RetryTemplate retryTemplate,CachingConnectionFactory factory){
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setRetryTemplate(retryTemplate);
		rabbitTemplate.setConnectionFactory(factory);
		return rabbitTemplate;
	}

}
