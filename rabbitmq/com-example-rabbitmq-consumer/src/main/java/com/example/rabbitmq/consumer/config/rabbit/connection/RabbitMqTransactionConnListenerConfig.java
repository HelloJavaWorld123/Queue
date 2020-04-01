package com.example.rabbitmq.consumer.config.rabbit.connection;

import com.example.rabbitmq.consumer.config.properties.RabbitMqTranProperties;
import com.example.rabbitmq.consumer.config.rabbit.listener.CustomerChannelListener;
import com.example.rabbitmq.consumer.config.rabbit.listener.CustomerConnectionListener;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : RXK
 * Date : 2020/3/31 18:01
 * Desc: 事务链接工厂
 */
@Configuration
@EnableConfigurationProperties(value = {RabbitMqTranProperties.class})
public class RabbitMqTransactionConnListenerConfig{

	private final CustomerChannelListener channelListener;

	private final CustomerConnectionListener connectionListener;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final RabbitMqTranProperties transactionProperties;


	public RabbitMqTransactionConnListenerConfig(CustomerChannelListener channelListener, CustomerConnectionListener connectionListener, ThreadPoolTaskExecutor threadPoolTaskExecutor, RabbitMqTranProperties transactionProperties){
		this.channelListener = channelListener;
		this.connectionListener = connectionListener;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.transactionProperties = transactionProperties;
	}




	@Bean(name = "transactionSimpleRabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory transactionSimpleRabbitListenerContainerFactory(CachingConnectionFactory transactionCachingConnectionFactory,Jackson2JsonMessageConverter jackson2JsonMessageConverter){
		SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
		containerFactory.setConnectionFactory(transactionCachingConnectionFactory);
		containerFactory.setAutoStartup(Boolean.TRUE);
		PropertyMapper mapper = PropertyMapper.get();
		containerFactory.setConcurrentConsumers(transactionProperties.getConcurrentConsumer());
		containerFactory.setMaxConcurrentConsumers(transactionProperties.getMaxConcurrentConsumer());

		//事务类型的监听器 每次只获取一条消息
		mapper.from(transactionProperties::getPrefetchCount).to(containerFactory::setPrefetchCount);
		//当前监听器支持 事务
		mapper.from(transactionProperties::getChannelTransacted).to(containerFactory::setChannelTransacted);

		containerFactory.setDefaultRequeueRejected(Boolean.TRUE);
		//nested exception is org.springframework.amqp.UncategorizedAmqpException: java.lang.IllegalStateException: When 'mismatchedQueuesFatal' is 'true', there must be exactly one AmqpAdmin in the context or you must inject one into this container; found: 0
//		containerFactory.setMissingQueuesFatal(Boolean.TRUE);
//		containerFactory.setMismatchedQueuesFatal(Boolean.TRUE);

		containerFactory.setBatchSize(1);
		containerFactory.setConsumerBatchEnabled(Boolean.FALSE);
		containerFactory.setDeBatchingEnabled(Boolean.FALSE);
		containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		//当消息被拒绝时 是否重新入队
		containerFactory.setDefaultRequeueRejected(Boolean.TRUE);

//		containerFactory.setMessageConverter(jackson2JsonMessageConverter);
		containerFactory.setTaskExecutor(threadPoolTaskExecutor);

		containerFactory.setChannelTransacted(Boolean.TRUE);
		//默认值 10s
		containerFactory.setStartConsumerMinInterval(10000L);
		//默认值 1min
		containerFactory.setStopConsumerMinInterval(60000L);

		//默认值 10
		containerFactory.setConsecutiveActiveTrigger(5);
		//默认值 10
		containerFactory.setConsecutiveIdleTrigger(20);
		return containerFactory;
	}


	@Bean(name = "transactionCachingConnectionFactory")
	public CachingConnectionFactory transactionCachingConnectionFactory(ConnectionFactory transactionRabbitConnectionFactory){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(transactionRabbitConnectionFactory);

		cachingConnectionFactory.setCacheMode(transactionProperties.getCache().getConnection().getMode());
		cachingConnectionFactory.setChannelCacheSize(transactionProperties.getCache().getChannel().getSize());
		cachingConnectionFactory.setChannelCheckoutTimeout(transactionProperties.getCache().getChannel().getCheckoutTimeout().toMillis());

		cachingConnectionFactory.setConnectionTimeout(transactionProperties.getConnectionTimeout());

		cachingConnectionFactory.setPublisherReturns(transactionProperties.getPublisherReturns());
		cachingConnectionFactory.setPublisherConfirmType(transactionProperties.getPublisherConfirmType());

		cachingConnectionFactory.setRequestedHeartBeat(transactionProperties.getRequestedHeartbeat());

		cachingConnectionFactory.setExecutor(threadPoolTaskExecutor);
		cachingConnectionFactory.addConnectionListener(connectionListener);
		cachingConnectionFactory.addChannelListener(channelListener);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}


	@Bean(name = "transactionRabbitConnectionFactory")
	public ConnectionFactory transactionRabbitConnectionFactory(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		PropertyMapper mapper = PropertyMapper.get();
		mapper.from(transactionProperties::getHost).whenHasText().to(connectionFactory::setHost);
		mapper.from(transactionProperties::getPort).whenNonNull().to(connectionFactory::setPort);
		mapper.from(transactionProperties::getUserName).whenHasText().to(connectionFactory::setUsername);
		mapper.from(transactionProperties::getPassword).whenHasText().to(connectionFactory::setPassword);
		mapper.from(transactionProperties::getVirtualHost).whenHasText().to(connectionFactory::setVirtualHost);
		mapper.from(transactionProperties::getRequestedHeartbeat).to(connectionFactory::setRequestedHeartbeat);
		return connectionFactory;
	}

}
