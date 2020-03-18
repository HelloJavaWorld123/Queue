package com.example.rabbitmq.consumer.config;

import com.example.rabbit.common.enums.RabbitMqEnum;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

	@Autowired
	private RabbitProperties rabbitProperties;

	@Autowired
	private CustomerChannelListener customerChannelListener;

	@Autowired
	private CustomerConnectionListener customerConnectionListener;

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

	@Bean
	@ConditionalOnMissingBean(RabbitTemplate.class)
	public RabbitTemplate rabbitTemplate(RetryTemplate retryTemplate,CachingConnectionFactory factory){
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setRetryTemplate(retryTemplate);
		rabbitTemplate.setConnectionFactory(factory);
		rabbitTemplate.setEncoding("UTF-8");
		rabbitTemplate.afterPropertiesSet();
		return rabbitTemplate;
	}

	@Bean
	public DirectExchange directExchange(){
		return new DirectExchange(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(),true,false);
	}

	@Bean
	public Queue queue(){
		return new Queue(RabbitMqEnum.QueueEnum.TEST_QUEUE.name(),true,false,false);
	}

	@Bean
	public Binding binding(){
		return BindingBuilder.bind(queue()).to(directExchange()).with(RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name());
	}


	@Bean
	public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate, DirectExchange directExchange,Queue queue,Binding binding){
		RabbitAdmin admin = new RabbitAdmin(rabbitTemplate);
		admin.setAutoStartup(Boolean.TRUE);
		admin.declareExchange(directExchange);
		admin.declareQueue(queue);
		admin.declareBinding(binding);
		admin.afterPropertiesSet();
		return admin;
	}


}
