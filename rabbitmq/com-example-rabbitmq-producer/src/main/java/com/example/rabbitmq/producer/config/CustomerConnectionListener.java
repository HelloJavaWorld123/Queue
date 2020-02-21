package com.example.rabbitmq.producer.config;

import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/2/21 16:20
 * Desc: rabbitmq 链接监听器
 */
@Component
public class CustomerConnectionListener extends AbstractConfig implements ConnectionListener{

	public void onCreate(Connection connection){
		LOGGER.info("在：{}成功创建链接,是否打开：{}",connection.getLocalPort(),connection.isOpen());
	}

	public void onClose(Connection connection){
		LOGGER.info("在：{},链接关闭：{}",connection.getLocalPort(),connection);
	}

	public void onShutDown(ShutdownSignalException signal){
		LOGGER.info("强制关闭了一个链接或者通道：{}",signal.getLocalizedMessage());
	}
}
