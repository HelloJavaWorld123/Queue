package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/4/8 13:25
 * Desc:
 */
@Component
public class RocketMqProducerStartUp implements SmartLifecycle{


	private volatile boolean start = false;

	private final DefaultMQProducer defaultMQProducer;

	public RocketMqProducerStartUp(DefaultMQProducer defaultMQProducer){
		this.defaultMQProducer = defaultMQProducer;
	}


	@Override
	public void start(){
		if(!start){
			try{
				LogUtils.info("启动Producer");
				defaultMQProducer.start();
				start = true;
			} catch(MQClientException e){
				LogUtils.error("启动 Producer 异常：", e);
				start = false;
			}
			LogUtils.info("启动Producer成功");
		}
	}

	@Override
	public void stop(){
		if(Objects.nonNull(defaultMQProducer)){
			if(!start){
				LogUtils.info("启动Producer");
				defaultMQProducer.shutdown();
				start = false;
			}
		}
	}


	@Override
	public boolean isRunning(){
		return start;
	}

	@Override
	public boolean isAutoStartup(){
		return true;
	}

	@Override
	public int getPhase(){
		return Integer.MAX_VALUE;
	}
}
