package com.example.rabbitmq.consumer.config.properties;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : RXK
 * Date : 2020/4/1 10:11
 * Desc:
 */
@Data
@ConfigurationProperties(prefix = "spring.log.rabbitmq")
public class RabbitMqLogProperties{

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

	private CachingConnectionFactory.ConfirmType confirmType;

	private RabbitProperties.Cache cache;

	private Integer batchSize;

	private Integer consecutiveActiveTrigger;

	private Integer consecutiveIdleTrigger;

	private Long startConsumerMinInterval;

	private Long stopConsumerMinInterval;
}
