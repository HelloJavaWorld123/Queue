package com.example.rabbitmq.producer.config;

import com.example.rabbitmq.producer.config.exception.CustomerRabbitExceptionHandler;
import com.example.rabbitmq.producer.config.listener.CustomerChannelListener;
import com.example.rabbitmq.producer.config.listener.CustomerConnectionListener;
import com.example.rabbitmq.producer.config.properties.RabbitTransactionConfigProperties;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.CharSetUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author : RXK
 * Date : 2020/3/31 14:15
 * Desc: 事务性的链接工厂配置
 */
@EnableRabbit
@Configuration
@EnableConfigurationProperties(RabbitTransactionConfigProperties.class)
public class RabbitTransactionConnectionConfig{


	private final Environment environment;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final CustomerChannelListener customerChannelListener;

	private final CustomerRabbitExceptionHandler exceptionHandler;

	private final RabbitTransactionConfigProperties configProperties;

	private final CustomerConnectionListener customerConnectionListener;

	public RabbitTransactionConnectionConfig(Environment environment, ThreadPoolTaskExecutor threadPoolTaskExecutor, CustomerChannelListener customerChannelListener, CustomerRabbitExceptionHandler exceptionHandler, RabbitTransactionConfigProperties configProperties, CustomerConnectionListener customerConnectionListener){
		this.environment = environment;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.customerChannelListener = customerChannelListener;
		this.exceptionHandler = exceptionHandler;
		this.configProperties = configProperties;
		this.customerConnectionListener = customerConnectionListener;
	}

	@Bean(destroyMethod = "destroy",initMethod = "start",name = "transactionalRabbitTemplate")
	public RabbitTemplate transactionalRabbitTemplate(CachingConnectionFactory transactionCachingConnectionFactory,Jackson2JsonMessageConverter jsonMessageConverter){
		RabbitTemplate rabbitTemplate = new RabbitTemplate(transactionCachingConnectionFactory);
		rabbitTemplate.setEncoding(Charset.defaultCharset().name());
		rabbitTemplate.setMandatory(configProperties.getMandatory());
		rabbitTemplate.setChannelTransacted(Boolean.TRUE);
		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		rabbitTemplate.afterPropertiesSet();
		return rabbitTemplate;
	}


	@Bean(name = "transactionalRetryTemplate")
	public RetryTemplate transactionalRetryTemplate(){
		RetryTemplate retryTemplate = null;
		if(configProperties.getRetry().isEnabled()){
			retryTemplate = new RetryTemplate();

			ExponentialRandomBackOffPolicy randomBackOffPolicy = new ExponentialRandomBackOffPolicy();
			randomBackOffPolicy.setInitialInterval(10000);
			randomBackOffPolicy.setMaxInterval(120000);
			randomBackOffPolicy.setMultiplier(1.5);
			retryTemplate.setBackOffPolicy(randomBackOffPolicy);

			TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
			timeoutRetryPolicy.setTimeout(60000);
			retryTemplate.setRetryPolicy(timeoutRetryPolicy);
			retryTemplate.setThrowLastExceptionOnExhausted(Boolean.TRUE);

		}
		return retryTemplate;
	}


	@Bean(name = "transactionCachingConnectionFactory",destroyMethod = "destroy")
	public CachingConnectionFactory transactionCachingConnectionFactory(ConnectionFactory transactionRabbitConnectionFactory,SimplePropertyValueConnectionNameStrategy transactionalConnectionNameStrategy){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(transactionRabbitConnectionFactory);
		PropertyMapper mapper = PropertyMapper.get();
		//设置Broker 以及 consumer的确认模式
		mapper.from(configProperties::getPublisherReturns).whenNonNull().to(cachingConnectionFactory::setPublisherReturns);
		//该模式 与 事务 是互斥 不能共存 这种模式时异步的，在等待RabbitMQ返回确认时 会发送其他的消息 而事务模式中发送消息后 会阻塞等待Rabbitmq返回确认消息
		mapper.from(configProperties::getPublisherConfirmType).whenNonNull().to(cachingConnectionFactory::setPublisherConfirmType);

		//设置缓存模式
		RabbitProperties.Cache cache = configProperties.getCache();
		mapper.from(cache.getConnection()::getMode).whenNonNull().to(cachingConnectionFactory::setCacheMode);
		mapper.from(cache.getChannel()::getSize).to(cachingConnectionFactory::setChannelCacheSize);
		mapper.from(cache.getChannel()::getCheckoutTimeout).whenNonNull().asInt(Duration::toMillis).to(cachingConnectionFactory::setChannelCheckoutTimeout);

		//TCP 链接的超时时间
		cachingConnectionFactory.setConnectionTimeout(60000);
		//关闭链接的超时时间
		cachingConnectionFactory.setCloseTimeout(30000);
		//channel链接监听器
		cachingConnectionFactory.addChannelListener(customerChannelListener);
		//链接监听器
		cachingConnectionFactory.addConnectionListener(customerConnectionListener);

		//设置任务执行的线程池
		cachingConnectionFactory.setExecutor(threadPoolTaskExecutor);

		cachingConnectionFactory.setConnectionNameStrategy(transactionalConnectionNameStrategy);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}


	@Bean(name = "transactionRabbitConnectionFactory")
	public ConnectionFactory connectionFactory(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		PropertyMapper mapper = PropertyMapper.get();
		mapper.from(configProperties::getHost).whenHasText().to(connectionFactory::setHost);
		mapper.from(configProperties::getPort).whenNonNull().to(connectionFactory::setPort);
		mapper.from(configProperties::getUserName).whenHasText().to(connectionFactory::setUsername);
		mapper.from(configProperties::getPassword).whenHasText().to(connectionFactory::setPassword);
		mapper.from(configProperties::getVirtualHost).whenHasText().to(connectionFactory::setVirtualHost);
		mapper.from(configProperties::getRequestedHeartbeat).whenNonNull().to(connectionFactory::setRequestedHeartbeat);

		connectionFactory.setExceptionHandler(exceptionHandler);
		return connectionFactory;
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter(){
		Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
		messageConverter.setDefaultCharset(StandardCharsets.UTF_8.name());
		return messageConverter;
	}


	@Bean(name = "transactionalConnectionNameStrategy")
	public SimplePropertyValueConnectionNameStrategy connectionNameStrategy(){
		SimplePropertyValueConnectionNameStrategy strategy = new SimplePropertyValueConnectionNameStrategy("spring.transaction.application.name");
		strategy.setEnvironment(environment);
		return strategy;
	}

	@Bean
	public RabbitTransactionManager rabbitTransactionManager(CachingConnectionFactory transactionCachingConnectionFactory){
		return new RabbitTransactionManager(transactionCachingConnectionFactory);
	}

}
