package com.example.rabbitmq.producer.config;

import com.example.rabbitmq.producer.enums.RabbitMqEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

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

	private final CustomerChannelListener customerChannelListener;

	private final CustomerConnectionListener customerConnectionListener;

	public RabbitMqConfig(RabbitProperties properties, CustomerChannelListener customerChannelListener, CustomerConnectionListener customerConnectionListener){
		this.properties = properties;
		this.customerChannelListener = customerChannelListener;
		this.customerConnectionListener = customerConnectionListener;
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
		rabbitTemplate.afterPropertiesSet();
		return rabbitTemplate;
	}


	@Bean(destroyMethod = "destroy")
	@ConditionalOnMissingBean(value = CachingConnectionFactory.class)
	public CachingConnectionFactory cachingConnectionFactory() throws Exception{
		CachingConnectionFactory factory = new CachingConnectionFactory(Objects.requireNonNull(rabbitConnectionFactoryBean().getObject()));
		factory.setPublisherReturns(properties.isPublisherReturns());
		factory.setPublisherConfirmType(properties.getPublisherConfirmType());
		factory.setCacheMode(properties.getCache().getConnection().getMode());
		factory.setChannelCacheSize(properties.getCache().getChannel().getSize());

		//添加自定义的channel监听器
		factory.addChannelListener(customerChannelListener);
		//添加自定义的connection的监听器
		factory.addConnectionListener(customerConnectionListener);
		factory.setConnectionLimit(10);

		factory.afterPropertiesSet();
		return factory;
	}

	RabbitConnectionFactoryBean rabbitConnectionFactoryBean(){
		RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
		PropertyMapper mapper = PropertyMapper.get();
		mapper.from(properties::getHost).to(factoryBean::setHost);
		mapper.from(properties::getPort).to(factoryBean::setPort);
		mapper.from(properties::getUsername).to(factoryBean::setUsername);
		mapper.from(properties::getPassword).to(factoryBean::setPassword);
		mapper.from(properties::getVirtualHost).to(factoryBean::setVirtualHost);
		mapper.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds).to(factoryBean::setRequestedHeartbeat);
		RabbitProperties.Ssl ssl = properties.getSsl();
		mapper.from(ssl::isEnabled).to(factoryBean::setUseSSL);
		mapper.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis).to(factoryBean::setConnectionTimeout);
		factoryBean.afterPropertiesSet();
		return factoryBean;
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
	public Binding binding(){
		return BindingBuilder.bind(queue()).to(directExchange()).with(RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY.name());
	}

	/**
	 * TODO  这玩意干嘛使得？？？？
	 */
	@Bean
	@ConditionalOnMissingBean(value = RabbitAdmin.class)
	public RabbitAdmin rabbitAdmin() throws Exception{
		RabbitAdmin admin = new RabbitAdmin(cachingConnectionFactory());
		admin.setAutoStartup(true);
		admin.declareExchange(directExchange());
		admin.declareQueue(queue());
		admin.declareBinding(binding());
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
			policy.setMaxAttempts(properties.getTemplate().getRetry().getMaxAttempts());
			retryTemplate.setRetryPolicy(policy);



		}
		return retryTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(AsyncRabbitTemplate.class)
	public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate){
		return new AsyncRabbitTemplate(rabbitTemplate);
	}


}
