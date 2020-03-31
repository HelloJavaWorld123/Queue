package com.example.rabbitmq.producer.config;

import com.example.rabbitmq.producer.config.exception.CustomerRabbitExceptionHandler;
import com.example.rabbitmq.producer.config.listener.CustomerChannelListener;
import com.example.rabbitmq.producer.config.listener.CustomerConnectionListener;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.BrokerEventListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author : RXK
 * Date : 2020/2/21 14:15
 * Desc:
 */
@EnableRabbit
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitMqConnectionConfig{

	private final Environment environment;

	private final RabbitProperties properties;

	private final CustomerChannelListener customerChannelListener;

	private final CustomerConnectionListener customerConnectionListener;

	private final CustomerRabbitExceptionHandler customerRabbitExceptionHandler;

	public RabbitMqConnectionConfig(Environment environment, RabbitProperties properties, CustomerChannelListener customerChannelListener, CustomerConnectionListener customerConnectionListener, CustomerRabbitExceptionHandler customerRabbitExceptionHandler){
		this.environment = environment;
		this.properties = properties;
		this.customerChannelListener = customerChannelListener;
		this.customerConnectionListener = customerConnectionListener;
		this.customerRabbitExceptionHandler = customerRabbitExceptionHandler;
	}




	@Bean(initMethod = "start",
	      destroyMethod = "destroy")
	@ConditionalOnMissingBean(value = RabbitTemplate.class)
	public RabbitTemplate rabbitTemplate(CachingConnectionFactory cachingConnectionFactory,RetryTemplate retryTemplate,Jackson2JsonMessageConverter jackson2JsonMessageConverter){
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setEncoding("UTF-8");
		rabbitTemplate.setConnectionFactory(cachingConnectionFactory);

		rabbitTemplate.setRetryTemplate(retryTemplate);
		rabbitTemplate.setMandatory(properties.getTemplate().getMandatory());
		rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
		rabbitTemplate.afterPropertiesSet();
		return rabbitTemplate;
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



	@Bean
	@ConditionalOnMissingBean(MessageConverter.class)
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter(DefaultClassMapper defaultClassMapper){
		Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
		messageConverter.setClassMapper(defaultClassMapper);
		messageConverter.setDefaultCharset(StandardCharsets.UTF_8.name());
		return messageConverter;
	}

	@Bean
	@ConditionalOnMissingBean(ClassMapper.class)
	public DefaultClassMapper defaultClassMapper(){
		DefaultClassMapper defaultClassMapper = new DefaultClassMapper();
		defaultClassMapper.afterPropertiesSet();
		return defaultClassMapper;
	}


	@Bean(destroyMethod = "destroy")
	public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory connectionFactory){
		CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
		factory.setPublisherReturns(properties.isPublisherReturns());
		factory.setPublisherConfirmType(properties.getPublisherConfirmType());
		factory.setCacheMode(properties.getCache().getConnection().getMode());
		factory.setChannelCacheSize(properties.getCache().getChannel().getSize());
		factory.setConnectionNameStrategy(simplePropertyValueConnectionNameStrategy());
		factory.addChannelListener(customerChannelListener);
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
	public SimplePropertyValueConnectionNameStrategy simplePropertyValueConnectionNameStrategy(){
		SimplePropertyValueConnectionNameStrategy strategy = new SimplePropertyValueConnectionNameStrategy("spring.application.name");
		strategy.setEnvironment(environment);
		return strategy;
	}
}
