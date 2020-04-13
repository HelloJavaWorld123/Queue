package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.IpUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : RXK
 * Date : 2020/4/7 15:09
 * Desc: 配置 RocketMq 的 DefaultRocketMqProducer
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.rocketmq",
                       name = "nameServerAddress",
                       matchIfMissing = true)
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqAutoConfig{

	private final RocketMqConfigProperties rocketMqConfigProperties;

	public RocketMqAutoConfig(RocketMqConfigProperties rocketMqConfigProperties){
		this.rocketMqConfigProperties = rocketMqConfigProperties;
	}


	@Bean
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "producer.enable",
	                       matchIfMissing = false)
	@SuppressWarnings("uncheck")
	public DefaultMQProducer defaultMqProducer(){
		if(rocketMqConfigProperties.getProducer().isEnable()){
			DefaultMQProducer producer = new DefaultMQProducer();
			commonProducerConfig(producer);
			return producer;
		}
		return null;
	}


	@Bean(name = "transactionMqProducer")
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "producer.transactionEnable",
	                       matchIfMissing = false)
	public TransactionMQProducer transactionMqProducer(){
		TransactionMQProducer transactionMqProducer = new TransactionMQProducer();
		commonProducerConfig(transactionMqProducer);


		//事务句柄的参数设置
		transactionMqProducer.setExecutorService(threadPoolTaskExecutor());

		return transactionMqProducer;
	}


	private void commonProducerConfig(DefaultMQProducer producer){
		producer.setNamesrvAddr(getNameSrvAddr());

		producer.setInstanceName(getInstanceName());
		producer.setUnitName(getUnitName());

		producer.setClientCallbackExecutorThreads(rocketMqConfigProperties.getClientCallBackExecutorThreads());

		producer.setNamespace(rocketMqConfigProperties.getNameSpace());

		producer.setPollNameServerInterval(rocketMqConfigProperties.getPollNameServerInterval());
		producer.setHeartbeatBrokerInterval(rocketMqConfigProperties.getHeartbeatBrokerInterval());

		producer.setProducerGroup(rocketMqConfigProperties.getProducer().getProducerGroup());
		producer.setDefaultTopicQueueNums(rocketMqConfigProperties.getProducer().getDefaultTopicQueueNums());
		producer.setRetryTimesWhenSendAsyncFailed(rocketMqConfigProperties.getProducer().getRetryTimesWhenSendAsyncFailed());
		producer.setRetryAnotherBrokerWhenNotStoreOK(rocketMqConfigProperties.getProducer().isRetryAnOtherBrokerWhenNotStoreOk());
	}


	private String getUnitName(){
		String unitName = rocketMqConfigProperties.getUnitName();
		if(StringUtils.isNotEmpty(unitName)){
			return unitName;
		} else{
			return null;
		}
	}


	private String getInstanceName(){
		String instanceName = rocketMqConfigProperties.getInstanceName();
		if(StringUtils.isNotEmpty(instanceName)){
			return instanceName;
		} else if("DEFAULT".equals(instanceName)){
			return IpUtils.ipAndName();
		} else if(StringUtils.isEmpty(instanceName)){
			return String.valueOf(MixAll.getPID());
		} else{
			return null;
		}
	}

	private String getNameSrvAddr(){
		String nameServerAddress = rocketMqConfigProperties.getNameServerAddress();
		if(StringUtils.isEmpty(nameServerAddress)){
			throw new IllegalArgumentException("RocketMQ Producer NameServer Address Con't Be NULL");
		}
		return nameServerAddress;
	}


	@Bean
	public ThreadPoolExecutor threadPoolTaskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		RocketMqConfigProperties.ConsumerThreadPool threadPool = this.rocketMqConfigProperties.getConsumer().getThreadPool();
		executor.setCorePoolSize(threadPool.getCorePoolSize());
		executor.setMaxPoolSize(threadPool.getMaxPoolSize());
		executor.setKeepAliveSeconds(threadPool.getKeepLiveSeconds());
		executor.setRejectedExecutionHandler(threadPool.getRejectedExecutionHandler());
		executor.setWaitForTasksToCompleteOnShutdown(threadPool.isWaitForTasksToCompleteWhenShutDown());
		executor.setAllowCoreThreadTimeOut(threadPool.isAllowCoreThreadTimeOut());
		executor.setAwaitTerminationSeconds(threadPool.getAwaitTerminationForSeconds());
		executor.initialize();
		return executor.getThreadPoolExecutor();
	}

}
