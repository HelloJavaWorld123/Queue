package com.example.rabbitmq.consumer.config.rabbit.listener;

import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/18 15:40
 * Desc: Connection Listener
 * 发送到 日志中心
 */
@Component
public class CustomerConnectionListener implements ConnectionListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerConnectionListener.class);

	@Override
	public void onCreate(Connection connection){
		LOGGER.info("---------------Create Connection:{}-----------",connection);
	}

	@Override
	public void onClose(Connection connection){
		LOGGER.info("---------------Close Connection:{}------------",connection);
	}

	@Override
	public void onShutDown(ShutdownSignalException signal){
		LOGGER.error("---------------On Shut Down:{0}------",signal);
	}
}
