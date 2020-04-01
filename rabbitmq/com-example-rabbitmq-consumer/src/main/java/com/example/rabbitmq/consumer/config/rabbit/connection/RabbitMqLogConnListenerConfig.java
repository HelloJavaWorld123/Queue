package com.example.rabbitmq.consumer.config.rabbit.connection;

import com.example.rabbitmq.consumer.config.properties.RabbitMqLogProperties;
import com.example.rabbitmq.consumer.config.rabbit.listener.CustomerChannelListener;
import com.example.rabbitmq.consumer.config.rabbit.listener.CustomerConnectionListener;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : RXK
 * Date : 2020/4/1 10:09
 * Desc: 日志监听器 链接工厂的配置
 */
@Configuration
@EnableConfigurationProperties(value = {RabbitMqLogProperties.class})
public class RabbitMqLogConnListenerConfig{

	private final Environment environment;

	private final CustomerChannelListener channelListener;

	private final CustomerConnectionListener connectionListener;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final RabbitMqLogProperties rabbitMqLogProperties;

	private static final String CONNECTION_NAME = "spring.log.application.name";

	public RabbitMqLogConnListenerConfig(Environment environment, CustomerChannelListener channelListener, CustomerConnectionListener connectionListener, ThreadPoolTaskExecutor threadPoolTaskExecutor, RabbitMqLogProperties rabbitMqLogProperties){
		this.environment = environment;
		this.channelListener = channelListener;
		this.connectionListener = connectionListener;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.rabbitMqLogProperties = rabbitMqLogProperties;
	}


	@Bean(name = "logRabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory logRabbitListenerContainerFactory(CachingConnectionFactory logCachingConnectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter){
		SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
		simpleRabbitListenerContainerFactory.setConnectionFactory(logCachingConnectionFactory);
		simpleRabbitListenerContainerFactory.setAutoStartup(Boolean.TRUE);

		//consumer 在一次请求中 Broker 发送多少条数据
		simpleRabbitListenerContainerFactory.setPrefetchCount(rabbitMqLogProperties.getPrefetchCount());

		//
//		simpleRabbitListenerContainerFactory.setBatchListener(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setBatchSize(rabbitMqLogProperties.getBatchSize());
//		simpleRabbitListenerContainerFactory.setDeBatchingEnabled(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setConsumerBatchEnabled(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setMessageConverter(jackson2JsonMessageConverter);

		simpleRabbitListenerContainerFactory.setConsecutiveActiveTrigger(rabbitMqLogProperties.getConsecutiveActiveTrigger());
		simpleRabbitListenerContainerFactory.setConsecutiveIdleTrigger(rabbitMqLogProperties.getConsecutiveIdleTrigger());
		simpleRabbitListenerContainerFactory.setStartConsumerMinInterval(rabbitMqLogProperties.getStartConsumerMinInterval());
		simpleRabbitListenerContainerFactory.setStopConsumerMinInterval(rabbitMqLogProperties.getStopConsumerMinInterval());

		//自动确认 和 手动确认不一致时 将会引起 unknown
		simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		simpleRabbitListenerContainerFactory.setDefaultRequeueRejected(Boolean.FALSE);

		simpleRabbitListenerContainerFactory.setChannelTransacted(Boolean.FALSE);

		simpleRabbitListenerContainerFactory.setConcurrentConsumers(rabbitMqLogProperties.getConcurrentConsumer());
		simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(rabbitMqLogProperties.getMaxConcurrentConsumer());
		return simpleRabbitListenerContainerFactory;
	}


	@Bean(name = "logCachingConnectionFactory")
	public CachingConnectionFactory logCachingConnectionFactory(ConnectionFactory rabbitLogConnectionFactory, SimplePropertyValueConnectionNameStrategy logConnectionNameStrategy){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitLogConnectionFactory);
		cachingConnectionFactory.setPublisherReturns(rabbitMqLogProperties.getPublisherReturns());
		cachingConnectionFactory.setPublisherConfirmType(rabbitMqLogProperties.getConfirmType());
		cachingConnectionFactory.setConnectionTimeout(rabbitMqLogProperties.getConnectionTimeout());
		cachingConnectionFactory.setCacheMode(rabbitMqLogProperties.getCache().getConnection().getMode());
		cachingConnectionFactory.setChannelCacheSize(rabbitMqLogProperties.getCache().getChannel().getSize());
		cachingConnectionFactory.setChannelCheckoutTimeout(rabbitMqLogProperties.getCache().getChannel().getCheckoutTimeout().toMillis());

		cachingConnectionFactory.addChannelListener(channelListener);
		cachingConnectionFactory.addConnectionListener(connectionListener);
		cachingConnectionFactory.setConnectionNameStrategy(logConnectionNameStrategy);
		cachingConnectionFactory.setExecutor(threadPoolTaskExecutor);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}


	@Bean(name = "rabbitLogConnectionFactory")
	public ConnectionFactory rabbitLogConnectionFactory(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		PropertyMapper propertyMapper = PropertyMapper.get();
		propertyMapper.from(rabbitMqLogProperties::getUserName).to(connectionFactory::setUsername);
		propertyMapper.from(rabbitMqLogProperties::getPassword).to(connectionFactory::setPassword);
		propertyMapper.from(rabbitMqLogProperties::getHost).to(connectionFactory::setHost);
		propertyMapper.from(rabbitMqLogProperties::getPort).to(connectionFactory::setPort);
		propertyMapper.from(rabbitMqLogProperties::getVirtualHost).to(connectionFactory::setVirtualHost);
		propertyMapper.from(rabbitMqLogProperties::getRequestedHeartbeat).to(connectionFactory::setRequestedHeartbeat);
		connectionFactory.setThreadFactory(threadPoolTaskExecutor);
		return connectionFactory;
	}

	@Bean(name = "logConnectionNameStrategy")
	public SimplePropertyValueConnectionNameStrategy logConnectionNameStrategy(){
		SimplePropertyValueConnectionNameStrategy connectionNameStrategy = new SimplePropertyValueConnectionNameStrategy(CONNECTION_NAME);
		connectionNameStrategy.setEnvironment(environment);
		return connectionNameStrategy;
	}


}
