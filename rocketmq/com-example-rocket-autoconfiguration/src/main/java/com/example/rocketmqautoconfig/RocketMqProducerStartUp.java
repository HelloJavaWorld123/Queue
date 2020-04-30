package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
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


	private volatile boolean producerStart = false;
	private volatile boolean transactionStart = false;

	@Autowired
	private RocketMqConfigProperties rocketMqConfigProperties;

	@Autowired(required = false)
	private  DefaultMQProducer defaultMQProducer;

	@Autowired(required = false)
	private TransactionMQProducer transactionMQProducer;


	@Override
	public void start(){
		//事务启动
		if(!transactionStart){
			if(rocketMqConfigProperties.getProducer().isTransactionEnable()){
				try{
					LogUtils.info("启动 transactionProducer 以及");
					transactionMQProducer.start();
					transactionStart = true;
					LogUtils.info("启动 transactionProducer 成功");
				} catch(MQClientException e){
					LogUtils.error("启动transactionProducer异常:",e);
					transactionStart = false;
				}
			}else{
				startProducer();
			}
		}


	}

	private void startProducer() {
		if(!producerStart){
			try{
				if(rocketMqConfigProperties.getProducer().isEnable()){
					LogUtils.info("启动Producer");
					defaultMQProducer.start();
					producerStart = true;
					LogUtils.info("启动Producer成功");
				}
			} catch(MQClientException e){
				LogUtils.error("启动 Producer 异常：", e);
				producerStart = false;
			}
		}
	}

	@Override
	public void stop(){

	}

	@Override
	public void stop(Runnable callback) {
		if(Objects.nonNull(defaultMQProducer)){
			if(!producerStart){
				LogUtils.info("启动Producer");
				defaultMQProducer.shutdown();
				producerStart = false;
			}
		}

		if (Objects.nonNull(transactionMQProducer)) {
			if(!transactionStart){
				LogUtils.info("开始停止 transactionProducer");
				transactionMQProducer.shutdown();
				transactionStart = false;
				LogUtils.info("停止 transactionProducer 成功");
			}
		}
		callback.run();
	}

	@Override
	public boolean isRunning(){
		return producerStart || transactionStart;
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
