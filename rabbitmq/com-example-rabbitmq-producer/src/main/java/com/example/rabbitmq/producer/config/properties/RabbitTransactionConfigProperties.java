package com.example.rabbitmq.producer.config.properties;

import lombok.Data;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : RXK
 * Date : 2020/3/31 13:53
 * Desc: Rabbit 事务链接池 配置
 */
@Data
@ConfigurationProperties(prefix = "spring.transaction.rabbitmq")
public class RabbitTransactionConfigProperties{

	private String host;

	private Integer port;

	private String userName;

	private String password;

	private String virtualHost;

	private Integer requestedHeartbeat;

	private Boolean publisherReturns;

	private CachingConnectionFactory.ConfirmType publisherConfirmType;

	private AcknowledgeMode acknowledgeMode;

	private RabbitProperties.Retry retry;

	private Boolean mandatory;

	private RabbitProperties.Cache cache;
}
