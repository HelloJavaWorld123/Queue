package com.example.rabbitmq.producer.config.message.transaction;

import com.example.rabbit.common.enums.RabbitMqEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : RXK
 * Date : 2020/3/31 16:10
 * Desc:
 */
@Configuration
public class RabbitMqTransactionMessageConfig{


	private final RetryTemplate transactionalRetryTemplate;

	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private final CachingConnectionFactory transactionCachingConnectionFactory;

	public RabbitMqTransactionMessageConfig(RetryTemplate transactionalRetryTemplate, ThreadPoolTaskExecutor threadPoolTaskExecutor, CachingConnectionFactory transactionCachingConnectionFactory){
		this.transactionalRetryTemplate = transactionalRetryTemplate;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.transactionCachingConnectionFactory = transactionCachingConnectionFactory;
	}

	@Bean
	public DirectExchange transactionDirectExchange(){
		return ExchangeBuilder
				.directExchange(RabbitMqEnum.ExchangeEnum.TEST_TRANSACTION_DIRECT_EXCHANGE.name())
				.durable(Boolean.TRUE)
				.build();
	}

	@Bean
	public Queue transactionQueue(){
		return QueueBuilder
				.durable(RabbitMqEnum.QueueEnum.TEST_TRANSACTION_QUEUE.name())
				.build();
	}

	@Bean
	public Binding transactionBinding(){
		return BindingBuilder.bind(transactionQueue()).to(transactionDirectExchange()).with(RabbitMqEnum.RoutingKey.TEST_TRANSACTION_KEY.name());
	}


	@Bean
	public RabbitAdmin transactionRabbitAdmin(){
		RabbitAdmin rabbitAdmin = new RabbitAdmin(transactionCachingConnectionFactory);
		rabbitAdmin.setRetryTemplate(transactionalRetryTemplate);
		rabbitAdmin.setTaskExecutor(threadPoolTaskExecutor);
		rabbitAdmin.setAutoStartup(Boolean.TRUE);

		rabbitAdmin.declareExchange(transactionDirectExchange());
		rabbitAdmin.declareQueue(transactionQueue());
		rabbitAdmin.declareBinding(transactionBinding());

		rabbitAdmin.afterPropertiesSet();
		return rabbitAdmin;

	}



}
