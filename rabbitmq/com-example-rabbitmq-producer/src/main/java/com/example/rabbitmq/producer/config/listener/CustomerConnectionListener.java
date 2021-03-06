package com.example.rabbitmq.producer.config.listener;

import com.example.rabbitmq.producer.config.AbstractConfig;
import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/2/21 16:20
 * Desc: rabbitmq 链接监听器
 * 发送到日志平台进行采集
 */
@Component
public class CustomerConnectionListener extends AbstractConfig implements ConnectionListener{

	@Override
	public void onCreate(Connection connection){
		LOGGER.info("创建的链接信息是：{}",connection.toString());
	}

	@Override
	public void onClose(Connection connection){
		LOGGER.info("在：{},链接关闭：{}",connection.getLocalPort(),connection);
	}

	@Override
	public void onShutDown(ShutdownSignalException signal){
		LOGGER.info("强制关闭了一个链接或者通道：{}",signal.getLocalizedMessage());
	}
}
