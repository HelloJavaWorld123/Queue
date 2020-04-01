package com.example.rabbitmq.consumer.config.properties;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : RXK
 * Date : 2020/3/31 18:02
 * Desc: RabbitMq事务 链接的配置
 */
@Data
@ConfigurationProperties(prefix = "spring.transaction.rabbitmq")
public class RabbitMqTranProperties{

	private String host;

	private Integer port;

	private String userName;

	private String password;

	private String virtualHost;

	private Boolean mandatory;

	private Integer prefetchCount;

	private Boolean channelTransacted;

	private Integer requestedHeartbeat;

	private Boolean publisherReturns;

	private Integer connectionTimeout;

	private Integer concurrentConsumer;

	private Integer maxConcurrentConsumer;

	private CachingConnectionFactory.ConfirmType publisherConfirmType;

	private RabbitProperties.Retry retry;

	private RabbitProperties.Cache cache;

}
