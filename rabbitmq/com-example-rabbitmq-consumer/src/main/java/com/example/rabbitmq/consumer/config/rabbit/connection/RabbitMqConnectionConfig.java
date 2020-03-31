package com.example.rabbitmq.consumer.config.rabbit.connection;

import com.example.rabbitmq.consumer.config.rabbit.handler.RabbitConnectionExceptionHandler;
import com.example.rabbitmq.consumer.config.rabbit.listener.CustomerChannelListener;
import com.example.rabbitmq.consumer.config.rabbit.listener.CustomerConnectionListener;
import com.example.rabbitmq.consumer.config.rabbit.handler.CustomerExceptionStrategy;
import com.example.rabbitmq.consumer.config.rabbit.strategy.CustomerConsumerTagStrategy;
import com.example.rabbitmq.consumer.modal.Person;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.ExponentialBackOff;

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
public class RabbitMqConnectionConfig{

	private final Environment environment;

	private final RabbitProperties rabbitProperties;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

	private final CustomerChannelListener customerChannelListener;

	private final CustomerExceptionStrategy customerExceptionStrategy;

	private final CustomerConnectionListener customerConnectionListener;

	private final CustomerConsumerTagStrategy customerConsumerTagStrategy;

	private final RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler;

	public RabbitMqConnectionConfig(Environment environment, RabbitProperties rabbitProperties, CustomerExceptionStrategy customerExceptionStrategy, ThreadPoolTaskExecutor threadPoolTaskExecutor, SimpleAsyncTaskExecutor simpleAsyncTaskExecutor, CustomerChannelListener customerChannelListener, CustomerConnectionListener customerConnectionListener, CustomerConsumerTagStrategy customerConsumerTagStrategy, RabbitConnectionExceptionHandler rabbitConnectionExceptionHandler){
		this.environment = environment;
		this.rabbitProperties = rabbitProperties;
		this.customerExceptionStrategy = customerExceptionStrategy;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.simpleAsyncTaskExecutor = simpleAsyncTaskExecutor;
		this.customerChannelListener = customerChannelListener;
		this.customerConnectionListener = customerConnectionListener;
		this.customerConsumerTagStrategy = customerConsumerTagStrategy;
		this.rabbitConnectionExceptionHandler = rabbitConnectionExceptionHandler;
	}


	/**
	 * 该Bean 的name(rabbitListenerContainerFactory) 是注解@RabbitListener的ContainerFacotory的默认取值
	 */
	/**
	 * 创建 SimpleMessageListenerContainer 的工厂
	 * <p>
	 * SMLC -- SimpleRabbitListenerContainerFactory
	 * 1.BatchSize
	 * 2.消费者的数量自动的缩容
	 * 3.自动缩容的消费者共用同一个线程
	 * DMLC -- DirectRabbitListenerContainerFactory
	 * 1.自动缩容的消费者线程间隔离，
	 */
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory, RetryTemplate retryTemplate, ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter){
		SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
		simpleRabbitListenerContainerFactory.setConnectionFactory(cachingConnectionFactory);
		simpleRabbitListenerContainerFactory.setAutoStartup(Boolean.TRUE);

		//设置当前消费者 从queue中一次性获取消息的数量,影响消费者的吞吐量 默认值250
//		simpleRabbitListenerContainerFactory.setPrefetchCount();

		//自定义任务执行的线程池
		simpleRabbitListenerContainerFactory.setTaskExecutor(simpleAsyncTaskExecutor);

		simpleRabbitListenerContainerFactory.setConcurrentConsumers(1);
		simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(8);

		simpleRabbitListenerContainerFactory.setReceiveTimeout(60000L);
		simpleRabbitListenerContainerFactory.setRetryTemplate(retryTemplate);

		simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		//当消费者不能处理该消息而拒绝时，将消息重新入队 默认值为true
//		simpleRabbitListenerContainerFactory.setDefaultRequeueRejected(Boolean.TRUE);

		//自定义错误处理器
//		simpleRabbitListenerContainerFactory.setErrorHandler(rabbitErrorHandler);
		simpleRabbitListenerContainerFactory.setErrorHandler(new ConditionalRejectingErrorHandler(customerExceptionStrategy));

		//消息与实体类之间序列化和反序列化方式
		//根据Content-Type 的类型 反序列化 消息的类类型
//		simpleRabbitListenerContainerFactory.setMessageConverter(contentTypeDelegatingMessageConverter);

		//以下两个互斥 链接的恢复策略
		//会以该时间间隔尝试恢复链接,使用 FixedBackOff
//		simpleRabbitListenerContainerFactory.setRecoveryInterval();
		//设置指定的恢复策略 递增的方式尝试
		simpleRabbitListenerContainerFactory.setRecoveryBackOff(new ExponentialBackOff());

		simpleRabbitListenerContainerFactory.setConsumerTagStrategy(customerConsumerTagStrategy);


		//容器空闲间隔是事件
//		simpleRabbitListenerContainerFactory.setIdleEventInterval();

		//在队列中的queue不能使用时 是否将容器快速失败机制
//		simpleRabbitListenerContainerFactory.setMissingQueuesFatal(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setMismatchedQueuesFatal(Boolean.TRUE);

		//消息批处理的配置
//		simpleRabbitListenerContainerFactory.setBatchListener(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setConsumerBatchEnabled(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setBatchSize(rabbitProperties.getListener().getSimple().getBatchSize());
//		simpleRabbitListenerContainerFactory.setDeBatchingEnabled(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setBatchingStrategy();


		//自定义线程池 默认为：SimpleAsyncTaskExecutor(线程不会复用,每一个任务创建一个新的线程)
		simpleRabbitListenerContainerFactory.setTaskExecutor(simpleAsyncTaskExecutor);

//		When 'mismatchedQueuesFatal' is 'true', there must be exactly one AmqpAdmin in the context or you must inject one into this container;
//		simpleRabbitListenerContainerFactory.setMismatchedQueuesFatal(rabbitProperties.getListener().getSimple().isMissingQueuesFatal());

		//RabbitMq的事务配置 当前的Channel是否是事务的
//		simpleRabbitListenerContainerFactory.setChannelTransacted(Boolean.TRUE);
//		simpleRabbitListenerContainerFactory.setTransactionManager();
		return simpleRabbitListenerContainerFactory;
	}

	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory){
		SimpleMessageListenerContainer simpleMessageListenerContainer = rabbitListenerContainerFactory.createListenerContainer();

		//默认值 为True
		simpleMessageListenerContainer.setAutoDeclare(Boolean.TRUE);
		simpleMessageListenerContainer.setDeclarationRetries(3);
		simpleMessageListenerContainer.setRetryDeclarationInterval(60000);
		simpleMessageListenerContainer.setFailedDeclarationRetryInterval(5000);

		//version 1.7.x之前默认值是true  version2.0 之后默认值是False.表示事务的消息在事务回滚时是否重新入队
		simpleMessageListenerContainer.setAlwaysRequeueWithTxManagerRollback(Boolean.TRUE);

		//default 300000ms
		simpleMessageListenerContainer.setShutdownTimeout(60000);
		//在shutDownTimeOut内不强制关闭通道,version2.0 以后默认值为True
		simpleMessageListenerContainer.setForceCloseChannel(Boolean.FALSE);
		//授权失败是否是致命的。如果是则在启动时,容器上下文不能被初始化.如果不是则会进入重试模式
		simpleMessageListenerContainer.setPossibleAuthenticationFailureFatal(Boolean.TRUE);
		//默认值为True 使消息进入DLQ
		simpleMessageListenerContainer.setDefaultRequeueRejected(Boolean.FALSE);
		return simpleMessageListenerContainer;
	}


	@Bean
	public ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter(Jackson2JsonMessageConverter jackson2JsonMessageConverter){
		ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
		Map<String, MessageConverter> contentTypeMessageConvert = new HashMap<>(2);
		contentTypeMessageConvert.put("application/json", jackson2JsonMessageConverter);
//		contentTypeMessageConvert.put("applicaiton/xml", jackson2XmlMessageConverter);
		messageConverter.setDelegates(contentTypeMessageConvert);
		return messageConverter;
	}


//	@Bean
//	public Jackson2XmlMessageConverter jackson2XmlMessageConverter(){
//		return new Jackson2XmlMessageConverter();
//	}

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


	@Bean(destroyMethod = "destroy")
	public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory connectionFactory,SimplePropertyValueConnectionNameStrategy strategy){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
		//自定义当前连接节点的名称标识
		cachingConnectionFactory.setConnectionNameStrategy(strategy);

		PropertyMapper mapper = PropertyMapper.get();
		RabbitProperties.Cache.Channel channel = rabbitProperties.getCache().getChannel();
		mapper.from(channel::getSize).to(cachingConnectionFactory::setChannelCacheSize);
		//决定了 channel 是否能被复用;当为0时，每次获取都会创建一个新的channel;否则在checkoutTimeOut时间内复用已有的channel链接
		mapper.from(channel::getCheckoutTimeout).whenNonNull().asInt(Duration::toMillis).to(cachingConnectionFactory::setChannelCheckoutTimeout);
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
	public ConnectionFactory connectionFactory(){
		ConnectionFactory connectionFactory = new ConnectionFactory();
		PropertyMapper propertyMapper = PropertyMapper.get();
		propertyMapper.from(rabbitProperties::getPort).to(connectionFactory::setPort);
		propertyMapper.from(rabbitProperties::getHost).to(connectionFactory::setHost);
		propertyMapper.from(rabbitProperties::getPassword).to(connectionFactory::setPassword);
		propertyMapper.from(rabbitProperties::getUsername).to(connectionFactory::setUsername);
		propertyMapper.from(rabbitProperties::getVirtualHost).to(connectionFactory::setVirtualHost);
		propertyMapper.from(rabbitProperties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds).to(connectionFactory::setRequestedHeartbeat);
		propertyMapper.from(rabbitProperties::getConnectionTimeout).whenNonNull().asInt(Duration::getSeconds).to(connectionFactory::setConnectionTimeout);
		connectionFactory.setAutomaticRecoveryEnabled(Boolean.TRUE);
		connectionFactory.setExceptionHandler(rabbitConnectionExceptionHandler);
		//当容器停止时 如果有消息没有处理完成，则最大等待多长时间后才关闭容器;默认值为10000ms 跟MessageListenerContainer 设置属性相同
//		connectionFactory.setShutdownTimeout(60000);
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
			mapper.from(rabbitProperties.getTemplate().getRetry()::getMaxInterval).whenNonNull().asInt(Duration::toMillis).to(exponentialBackOffPolicy::setMaxInterval);
			mapper.from(rabbitProperties.getTemplate().getRetry()::getInitialInterval).whenNonNull().asInt(Duration::toMillis).to(exponentialBackOffPolicy::setInitialInterval);
			retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
			retryTemplate.setThrowLastExceptionOnExhausted(Boolean.TRUE);
		}
		return retryTemplate;
	}

	@Bean
	public SimplePropertyValueConnectionNameStrategy strategy(){
		SimplePropertyValueConnectionNameStrategy strategy = new SimplePropertyValueConnectionNameStrategy("spring.application.name");
		strategy.setEnvironment(environment);
		return strategy;
	}

}
