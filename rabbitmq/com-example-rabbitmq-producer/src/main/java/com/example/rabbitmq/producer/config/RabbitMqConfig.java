package com.example.rabbitmq.producer.config;

import com.example.rabbit.common.enums.RabbitMqEnum;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
 * Date : 2020/2/21 14:15
 * Desc:
 */
@EnableRabbit
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitMqConfig{

	private final RabbitProperties properties;

	private final BrokerConfirmCallBack brokerConfirmCallBack;

	private final ConsumerReturnCallBack consumerReturnCallBack;

	private final CustomerChannelListener customerChannelListener;

	private final CustomerConnectionListener customerConnectionListener;

	private final CustomerRabbitExceptionHandler customerRabbitExceptionHandler;

	public RabbitMqConfig(RabbitProperties properties, BrokerConfirmCallBack brokerConfirmCallBack, ConsumerReturnCallBack consumerReturnCallBack, CustomerChannelListener customerChannelListener, CustomerConnectionListener customerConnectionListener, CustomerRabbitExceptionHandler customerRabbitExceptionHandler){
		this.properties = properties;
		this.brokerConfirmCallBack = brokerConfirmCallBack;
		this.consumerReturnCallBack = consumerReturnCallBack;
		this.customerChannelListener = customerChannelListener;
		this.customerConnectionListener = customerConnectionListener;
		this.customerRabbitExceptionHandler = customerRabbitExceptionHandler;
	}

	@Bean(initMethod = "start",
	      destroyMethod = "destroy")
	@ConditionalOnMissingBean(value = RabbitTemplate.class)
	public RabbitTemplate rabbitTemplate(CachingConnectionFactory cachingConnectionFactory,RetryTemplate retryTemplate){
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setEncoding("UTF-8");
		rabbitTemplate.setConnectionFactory(cachingConnectionFactory);

		//set RetryRabbitTemplate
		rabbitTemplate.setRetryTemplate(retryTemplate);
		rabbitTemplate.setMandatory(properties.getTemplate().getMandatory());

		rabbitTemplate.setConfirmCallback(brokerConfirmCallBack);
		rabbitTemplate.setReturnCallback(consumerReturnCallBack);
		rabbitTemplate.afterPropertiesSet();
		return rabbitTemplate;
	}


	@Bean(destroyMethod = "destroy")
	@ConditionalOnMissingBean(value = CachingConnectionFactory.class)
	public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory connectionFactory){
		CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
		factory.setPublisherReturns(properties.isPublisherReturns());
		factory.setPublisherConfirmType(properties.getPublisherConfirmType());
		factory.setCacheMode(properties.getCache().getConnection().getMode());
		factory.setChannelCacheSize(properties.getCache().getChannel().getSize());
		//由 ConfirmType的取缔,根据ConfirmType的不同,确认消息的从Broker的确认机制
//		factory.setPublisherConfirms(Boolean.TRUE);

		//添加自定义的channel监听器
		factory.addChannelListener(customerChannelListener);
		//添加自定义的connection的监听器
		factory.addConnectionListener(customerConnectionListener);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public ConnectionFactory connectionFactory(){
		ConnectionFactory factory = new ConnectionFactory();
		PropertyMapper mapper = PropertyMapper.get();
		mapper.from(properties::getHost).to(factory::setHost);
		mapper.from(properties::getPort).to(factory::setPort);
		mapper.from(properties::getUsername).to(factory::setUsername);
		mapper.from(properties::getPassword).to(factory::setPassword);
		mapper.from(properties::getVirtualHost).to(factory::setVirtualHost);
		mapper.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds).to(factory::setRequestedHeartbeat);
		mapper.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis).to(factory::setConnectionTimeout);
		factory.setExceptionHandler(customerRabbitExceptionHandler);
		factory.setAutomaticRecoveryEnabled(Boolean.TRUE);
		return factory;
	}


	@Bean
	public DirectExchange directExchange(){
		return new DirectExchange(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(), Boolean.TRUE, Boolean.FALSE);
	}

	@Bean
	public Queue queue(){
		return new Queue(RabbitMqEnum.QueueEnum.TEST_QUEUE.name(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
	}

	@Bean
	public Binding binding(Queue queue,DirectExchange directExchange){
		return BindingBuilder.bind(queue).to(directExchange).with(RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY);
	}


	@Bean
	@ConditionalOnMissingBean(value = RabbitAdmin.class)
	public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate,DirectExchange directExchange,Queue queue,Binding binding){
		/*
		*有两个构造方法:ConnectionFactory 和 RabbitTemplate;
		* 在RabbitTemplate 中 包含 RetryTemplate时,就会有区别.
		* */
		RabbitAdmin admin = new RabbitAdmin(rabbitTemplate);
		admin.setAutoStartup(true);
		admin.declareExchange(directExchange);
		admin.declareQueue(queue);
		admin.declareBinding(binding);
		admin.afterPropertiesSet();
		return admin;
	}

	@Bean
	@ConditionalOnMissingBean(RetryTemplate.class)
	public RetryTemplate retryTemplate(){
		RetryTemplate retryTemplate = null;
		if(properties.getTemplate().getRetry().isEnabled()){
			PropertyMapper mapper = PropertyMapper.get();
			retryTemplate = new RetryTemplate();
			SimpleRetryPolicy policy = new SimpleRetryPolicy();
			mapper.from(properties.getTemplate().getRetry()::getMaxAttempts).to(policy::setMaxAttempts);
			retryTemplate.setRetryPolicy(policy);
			ExponentialBackOffPolicy exponentialBackOff = new ExponentialBackOffPolicy();
			mapper.from(properties.getTemplate().getRetry()::getInitialInterval)
					.whenNonNull().asInt(Duration::toMillis).to(exponentialBackOff::setInitialInterval);
			mapper.from(properties.getTemplate().getRetry()::getMaxInterval).whenNonNull()
					.asInt(Duration::toMillis).to(exponentialBackOff::setMaxInterval);
			mapper.from(properties.getTemplate().getRetry()::getMultiplier).to(exponentialBackOff::setMultiplier);

			retryTemplate.setBackOffPolicy(exponentialBackOff);
			retryTemplate.setThrowLastExceptionOnExhausted(Boolean.TRUE);

		}
		return retryTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(AsyncRabbitTemplate.class)
	public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate){
		return new AsyncRabbitTemplate(rabbitTemplate);
	}


}
