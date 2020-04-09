package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author : RXK
 * Date : 2020/4/8 13:25
 * Desc:
 */
@Component
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqProducerStartUp implements SmartLifecycle{


	private volatile boolean start = false;

	private final DefaultMQProducer defaultMQProducer;

	private final RocketMqConfigProperties rocketMqConfigProperties;

	public RocketMqProducerStartUp(DefaultMQProducer defaultMQProducer, RocketMqConfigProperties rocketMqConfigProperties){
		this.defaultMQProducer = defaultMQProducer;
		this.rocketMqConfigProperties = rocketMqConfigProperties;
	}


	@Override
	public void start(){
		if(!start){
			try{
				if(rocketMqConfigProperties.getProducer().isEnable()){
					LogUtils.info("启动Producer");
					defaultMQProducer.start();
					start = true;
					LogUtils.info("启动Producer成功");
				}
			} catch(MQClientException e){
				LogUtils.error("启动 Producer 异常：", e);
				start = false;
			}
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
