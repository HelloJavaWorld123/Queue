package com.example.rabbitmq.consumer.config;

import com.example.rabbit.common.enums.RabbitMqEnum;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
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

	private final RabbitProperties rabbitProperties;

	private final CustomerChannelListener customerChannelListener;

	private final CustomerConnectionListener customerConnectionListener;

	private final RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler;

	public RabbitMqConfig(RabbitProperties rabbitProperties, CustomerChannelListener customerChannelListener, CustomerConnectionListener customerConnectionListener, RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler){
		this.rabbitProperties = rabbitProperties;
		this.customerChannelListener = customerChannelListener;
		this.customerConnectionListener = customerConnectionListener;
		this.rabbitConnectionExceptionHandler = rabbitConnectionExceptionHandler;
	}


	/**
	 * 该Bean 的name(rabbitListenerContainerFactory) 是注解@RabbitListener的ContainerFacotory的默认取值
	 */
	@Bean
	@ConditionalOnMissingBean(MessageListenerContainer.class)
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory,RetryTemplate retryTemplate){
		SimpleRabbitListenerContainerFactory listenerContainer = new SimpleRabbitListenerContainerFactory();
		listenerContainer.setConnectionFactory(cachingConnectionFactory);
		listenerContainer.setConcurrentConsumers(1);
		listenerContainer.setMaxConcurrentConsumers(8);
		listenerContainer.setReceiveTimeout(60000L);
		listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		listenerContainer.setRetryTemplate(retryTemplate);
//		listenerContainer.setMessageConverter();
		return listenerContainer;
	}


	@Bean(destroyMethod = "destroy")
	@ConditionalOnMissingBean(org.springframework.amqp.rabbit.connection.ConnectionFactory.class)
	public CachingConnectionFactory factory(ConnectionFactory connectionFactory){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
		PropertyMapper mapper = PropertyMapper.get();
		RabbitProperties.Cache.Channel channel = rabbitProperties.getCache().getChannel();
		mapper.from(channel::getSize).to(cachingConnectionFactory::setChannelCacheSize);
		mapper.from(channel::getCheckoutTimeout)
				.whenNonNull()
				.asInt(Duration::toMillis)
				.to(cachingConnectionFactory::setChannelCheckoutTimeout);
		RabbitProperties.Cache.Connection connection = rabbitProperties.getCache().getConnection();
		mapper.from(connection::getMode).to(cachingConnectionFactory::setCacheMode);
		/*当 Cache Mode 是 Channel时,该值不起作用*/
		/*When the cache mode is 'CHANNEL', the connection cache size cannot be configured*/
//		mapper.from(connection::getSize).to(cachingConnectionFactory::setConnectionCacheSize);
		cachingConnectionFactory.addChannelListener(customerChannelListener);
		cachingConnectionFactory.addConnectionListener(customerConnectionListener);
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


	/**
	 * 用于 消息确认的发送
	 */
	@Bean
	public RetryTemplate retryTemplate(){
		RetryTemplate retryTemplate = null;
		if(rabbitProperties.getTemplate().getRetry().isEnabled()){
			retryTemplate = new RetryTemplate();
			PropertyMapper mapper = PropertyMapper.get();
			SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
			mapper.from(rabbitProperties.getTemplate().getRetry()::getMaxAttempts).to(simpleRetryPolicy::setMaxAttempts);
			retryTemplate.setRetryPolicy(simpleRetryPolicy);
			ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
			mapper.from(rabbitProperties.getTemplate().getRetry()::getMultiplier).to(exponentialBackOffPolicy::setMultiplier);
			mapper.from(rabbitProperties.getTemplate().getRetry()::getMaxInterval)
					.whenNonNull()
					.asInt(Duration::toMillis)
					.to(exponentialBackOffPolicy::setMaxInterval);
			mapper.from(rabbitProperties.getTemplate().getRetry()::getInitialInterval)
					.whenNonNull()
					.asInt(Duration::toMillis)
					.to(exponentialBackOffPolicy::setInitialInterval);
			retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
		}
		return retryTemplate;
	}
}
