package com.example.rabbitmq.producer.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ShutDownChannelListener;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/2/21 16:09
 * Desc: 监听RabbitMq channel创建于销毁的监听器
 * 采用 kafka 发送到日志平台 进行采集
 */
@Component
public class CustomerChannelListener extends AbstractConfig implements ShutDownChannelListener{

	@Override
	public void onCreate(Channel channel, boolean transactional){
		LOGGER.info("--------RabbitMq 创建 Channel :{}, 是否有事务：{}----",channel,transactional);
	}

	@Override
	public void onShutDown(ShutdownSignalException signal){
		//signal.isHardError() --- true 的话 是指connection 关闭;false得话是指channel关闭
		LOGGER.info("接收到ShutDown信号：{}",signal.getLocalizedMessage());
	}
}
