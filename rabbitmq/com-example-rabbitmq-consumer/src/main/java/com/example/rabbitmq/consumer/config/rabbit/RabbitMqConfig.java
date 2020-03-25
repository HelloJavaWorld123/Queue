package com.example.rabbitmq.consumer.config.rabbit;

import com.example.rabbitmq.consumer.modal.Person;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.*;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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

	private final RabbitErrorHandler rabbitErrorHandler;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

	private final CustomerChannelListener customerChannelListener;

	private final CustomerConnectionListener customerConnectionListener;

	private final RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler;

	public RabbitMqConfig(RabbitProperties rabbitProperties, RabbitErrorHandler rabbitErrorHandler, ThreadPoolTaskExecutor threadPoolTaskExecutor, SimpleAsyncTaskExecutor simpleAsyncTaskExecutor, CustomerChannelListener customerChannelListener, CustomerConnectionListener customerConnectionListener, RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler){
		this.rabbitProperties = rabbitProperties;
		this.rabbitErrorHandler = rabbitErrorHandler;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.simpleAsyncTaskExecutor = simpleAsyncTaskExecutor;
		this.customerChannelListener = customerChannelListener;
		this.customerConnectionListener = customerConnectionListener;
		this.rabbitConnectionExceptionHandler = rabbitConnectionExceptionHandler;
	}


	/**
	 * 该Bean 的name(rabbitListenerContainerFactory) 是注解@RabbitListener的ContainerFacotory的默认取值
	 */
	/**
	 * SMLC -- SimpleRabbitListenerContainerFactory
	 * 1.BatchSize
	 * 2.消费者的数量自动的缩容
	 * 3.自动缩容的消费者共用同一个线程
	 * DMLC -- DirectRabbitListenerContainerFactory
	 * 1.自动缩容的消费者线程间隔离，
	 */
	@Bean
	@ConditionalOnMissingBean(MessageListenerContainer.class)
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory,RetryTemplate retryTemplate,ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter){
		SimpleRabbitListenerContainerFactory listenerContainer = new SimpleRabbitListenerContainerFactory();
		listenerContainer.setConnectionFactory(cachingConnectionFactory);
		listenerContainer.setAutoStartup(Boolean.TRUE);

		listenerContainer.setConcurrentConsumers(1);
		listenerContainer.setMaxConcurrentConsumers(8);

		listenerContainer.setReceiveTimeout(60000L);
		listenerContainer.setRetryTemplate(retryTemplate);
		//default 5000
		listenerContainer.setFailedDeclarationRetryInterval(5000L);

		listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		//当消费者不能处理该消息而拒绝时，将消息重新入队 默认值为true
		listenerContainer.setDefaultRequeueRejected(Boolean.TRUE);

		//自定义错误处理器
		listenerContainer.setErrorHandler(rabbitErrorHandler);

		//消息与实体类之间序列化和反序列化方式
		//根据Content-Type 的类型 反序列化 消息的类类型
		listenerContainer.setMessageConverter(contentTypeDelegatingMessageConverter);


		//容器空闲间隔是事件
//		listenerContainer.setIdleEventInterval();

		//在队列中的queue不能使用时 是否将容器快速失败机制
//		listenerContainer.setMissingQueuesFatal(Boolean.TRUE);

//		listenerContainer.setBatchListener(Boolean.TRUE);
//		listenerContainer.setConsumerBatchEnabled(Boolean.TRUE);
//		listenerContainer.setBatchSize(rabbitProperties.getListener().getSimple().getBatchSize());
		//自定义线程池 默认为：SimpleAsyncTaskExecutor(线程不会复用,每一个任务创建一个新的线程)

		listenerContainer.setTaskExecutor(simpleAsyncTaskExecutor);

		//TODO 如果不为空 则在 ConsumerFailed 或者 idleContainer 时 推送 ListenerContainerConsumerFailedEvent 事件
//		listenerContainer.setApplicationEventPublisher();

//		When 'mismatchedQueuesFatal' is 'true', there must be exactly one AmqpAdmin in the context or you must inject one into this container;
//		listenerContainer.setMismatchedQueuesFatal(rabbitProperties.getListener().getSimple().isMissingQueuesFatal());
		//TODO
//		listenerContainer.setChannelTransacted(true);
//		listenerContainer.setTransactionManager();
		return listenerContainer;
	}

	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory){
		SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
//		endpoint.setMessageListener(message -> {
//			message.getMessageProperties().setContentType("application/json");
//		});
		//默认使用的是SimpleAsyncTaskExecutor
		endpoint.setTaskExecutor(simpleAsyncTaskExecutor);
//		Message.addWhiteListPatterns("");
		return simpleRabbitListenerContainerFactory.createListenerContainer(endpoint);
	}


	@Bean
	public ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter(Jackson2JsonMessageConverter jackson2JsonMessageConverter,Jackson2XmlMessageConverter jackson2XmlMessageConverter){
		ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
		Map<String, MessageConverter> contentTypeMessageConvert = new HashMap<>(2);
		contentTypeMessageConvert.put("application/json", jackson2JsonMessageConverter);
		contentTypeMessageConvert.put("applicaiton/xml",jackson2XmlMessageConverter);
		messageConverter.setDelegates(contentTypeMessageConvert);
		return messageConverter;
	}


	@Bean
	public Jackson2XmlMessageConverter jackson2XmlMessageConverter(){
	  return new Jackson2XmlMessageConverter();
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter(DefaultClassMapper classMapper){
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		converter.setClassMapper(classMapper);
		return converter;
	}

	@Bean
	public DefaultClassMapper classMapper(){
		DefaultClassMapper classMapper = new DefaultClassMapper();
		//在消息的头部中：Type Id 的值,反序列成Person的类类型
		Map<String, Class<?>> idClassMapping = new HashMap<>(1);
		idClassMapping.put("person", Person.class);
		classMapper.setIdClassMapping(idClassMapping);
		classMapper.afterPropertiesSet();
		return classMapper;
	}


	/**
	 * TODO 集群
	 */
	@Bean(destroyMethod = "destroy")
	@ConditionalOnMissingBean(org.springframework.amqp.rabbit.connection.ConnectionFactory.class)
	public CachingConnectionFactory factory(ConnectionFactory connectionFactory){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
		PropertyMapper mapper = PropertyMapper.get();
		RabbitProperties.Cache.Channel channel = rabbitProperties.getCache().getChannel();
		mapper.from(channel::getSize).to(cachingConnectionFactory::setChannelCacheSize);
		//决定了 channel 是否能被复用;当为0时，每次获取都会创建一个新的channel;否则在checkoutTimeOut时间内复用已有的channel链接
		mapper.from(channel::getCheckoutTimeout)
				.whenNonNull()
				.asInt(Duration::toMillis)
				.to(cachingConnectionFactory::setChannelCheckoutTimeout);
		RabbitProperties.Cache.Connection connection = rabbitProperties.getCache().getConnection();
		mapper.from(connection::getMode).to(cachingConnectionFactory::setCacheMode);
		cachingConnectionFactory.addChannelListener(customerChannelListener);
		cachingConnectionFactory.addConnectionListener(customerConnectionListener);
		mapper.from(rabbitProperties::isPublisherReturns).to(cachingConnectionFactory::setPublisherReturns);
		mapper.from(rabbitProperties::getPublisherConfirmType).whenNonNull().to(cachingConnectionFactory::setPublisherConfirmType);



		cachingConnectionFactory.afterPropertiesSet();
		/*当 Cache Mode 是 Channel时,该值不起作用*/
		/*When the cache mode is 'CHANNEL', the connection cache size cannot be configured*/
//		mapper.from(connection::getSize).to(cachingConnectionFactory::setConnectionCacheSize);
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
		//当容器停止时 如果有消息没有处理完成，则最大等待多长时间后才关闭容器;默认值为10000ms
		connectionFactory.setShutdownTimeout(60000);
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
			retryTemplate.setThrowLastExceptionOnExhausted(Boolean.TRUE);
		}
		return retryTemplate;
	}
}
