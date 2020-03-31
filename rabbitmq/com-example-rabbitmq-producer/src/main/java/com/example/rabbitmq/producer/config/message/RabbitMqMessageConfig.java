package com.example.rabbitmq.producer.config.message;

import com.example.rabbit.common.enums.RabbitMqEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : RXK
 * Date : 2020/3/31 10:16
 * Desc:
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitMqMessageConfig{


	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

	public RabbitMqMessageConfig(ThreadPoolTaskExecutor threadPoolTaskExecutor){
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	@Bean
	public DirectExchange directExchange(){
		return new DirectExchange(RabbitMqEnum.ExchangeEnum.TEST_DIRECT_EXCHANGE.name(), Boolean.TRUE, Boolean.FALSE);
	}

	@Bean
	public FanoutExchange fanoutExchange(){
		return ExchangeBuilder
				.fanoutExchange(RabbitMqEnum.ExchangeEnum.TEST_FANOUT_EXCHANGE.name())
				.durable(Boolean.TRUE)
				.alternate("fanout_alternate")
				.withArgument("test_fanout_exchange", "fanout_exchange")
				.build();
	}


	@Bean
	public Queue queue(){
		return new Queue(RabbitMqEnum.QueueEnum.TEST_QUEUE.name(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
	}

	@Bean
	public Queue deadLetterArgQueue(){
		return QueueBuilder
				.durable(RabbitMqEnum.QueueEnum.TEST_X_DEAD_LETTER_EXCHANGE_ARG.name())
				.build();
	}


	@Bean
	public Queue logQueue(){
		return QueueBuilder.durable(RabbitMqEnum.QueueEnum.TEST_LOG_QUEUE.name())
				.deadLetterExchange(RabbitMqEnum.ExchangeEnum.TEST_DEAD_LETTER_EXCHANGE.name())
				.deadLetterRoutingKey(RabbitMqEnum.RoutingKey.TEST_DEAD_LETTER_EXCHANGE_KEY.name())
				.build();
	}

	@Bean
	public TopicExchange deadLetterExchange(){
		return ExchangeBuilder.topicExchange(RabbitMqEnum.ExchangeEnum.TEST_DEAD_LETTER_EXCHANGE.name()).durable(Boolean.TRUE).build();
	}

	@Bean
	public Binding deadLetterBinding(){
		return BindingBuilder
				.bind(deadLetterArgQueue())
				.to(deadLetterExchange())
				.with(RabbitMqEnum.RoutingKey.TEST_DEAD_LETTER_EXCHANGE_KEY.name());
	}

	@Bean
	public Binding binding(Queue queue,DirectExchange directExchange){
		return BindingBuilder.bind(queue).to(directExchange).with(RabbitMqEnum.RoutingKey.TEST_ROUTING_KEY);
	}


	/**
	 * QueueBuilder
	 */
	@Bean
	public Queue durableQueue(){
		return QueueBuilder.durable(RabbitMqEnum.QueueEnum.TEST_DURABLE_QUEUE.name()).withArgument("Durable_Queue","1234567890").build();
	}

	@Bean
	public Binding bindingDurable(Queue durableQueue,DirectExchange directExchange){
		return BindingBuilder.bind(durableQueue).to(directExchange).with(RabbitMqEnum.RoutingKey.TEST_ONE_EXCHANGE_BINDING_TWO_QUEUES.name());
	}


	/**
	 * 在启动时 RabbitAdmin会根据绑定的Queue、Exchange、binding调用RabbitTemplate,创建Queue,Exchange,Binding,Channel
	 * @code org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer#redeclareElementsIfNecessary()
	 * 如果设置了
	 * @code org.springframework.boot.autoconfigure.amqp.RabbitProperties.SimpleContainer#missingQueuesFatal()
	 * 则必须要有一个自定义的AmqpAdmin 否则启动报错
	 */
	@Bean
	public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate,Binding binding,Binding bindingDurable){
		/*
		 *有两个构造方法:ConnectionFactory 和 RabbitTemplate;
		 * 在RabbitTemplate 中 包含 RetryTemplate时,就会有区别.
		 * */
		RabbitAdmin admin = new RabbitAdmin(rabbitTemplate);
		admin.setAutoStartup(true);
		admin.setTaskExecutor(threadPoolTaskExecutor);
		admin.declareQueue(queue());
		admin.declareQueue(logQueue());
		admin.declareQueue(durableQueue());
		admin.declareQueue(deadLetterArgQueue());
		admin.declareExchange(deadLetterExchange());
		admin.declareExchange(directExchange());
		admin.declareExchange(fanoutExchange());
		admin.declareBinding(binding);
		admin.declareBinding(bindingDurable);
		admin.declareBinding(deadLetterBinding());
		admin.afterPropertiesSet();
		return admin;
	}

}
