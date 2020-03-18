package com.example.rabbitmq.consumer.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/18 15:46
 * Desc: Channel Lister
 * 发送到日志中心
 */
@Component
public class CustomerChannelListener implements ChannelListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerConnectionListener.class);

	@Override
	public void onCreate(Channel channel, boolean transactional){
		LOGGER.info("On Create Channel:{}, is Transactional:{}",channel,transactional);
	}

	@Override
	public void onShutDown(ShutdownSignalException signal){
		LOGGER.info("On ShutDown Channel:",signal);
	}
}
