package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.IpUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
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

	private final RocketMqConfigProperties threadPool;

	public RocketMqAutoConfig(RocketMqConfigProperties rocketMqConfigProperties){
		this.threadPool = rocketMqConfigProperties;
	}


	@Bean
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "producer.enable",
	                       matchIfMissing = false)
	@SuppressWarnings("uncheck")
	public DefaultMQProducer defaultMqProducer(){
		if(threadPool.getProducer().isEnable()){
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

	@Bean
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "consumer.enable",
	                       matchIfMissing = false)
	public DefaultMQPushConsumer defaultMqPushConsumer(){
		if(threadPool.getConsumer().isEnable()){
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
			RocketMqConfigProperties.Consumer propertiesConsumer = threadPool.getConsumer();
			consumer.setNamesrvAddr(threadPool.getNameServerAddress());
			consumer.setNamespace(threadPool.getNameSpace());
			consumer.setUnitName(threadPool.getUnitName());
			consumer.setUseTLS(threadPool.isUseTLS());
			consumer.setLanguage(threadPool.getLanguageCode());
			consumer.setHeartbeatBrokerInterval(threadPool.getHeartbeatBrokerInterval());
			consumer.setClientCallbackExecutorThreads(threadPool.getClientCallBackExecutorThreads());
			consumer.setPollNameServerInterval(threadPool.getPollNameServerInterval());
			consumer.setPullTimeDelayMillsWhenException(threadPool.getPullTimeDelayMillsWhenException());
			consumer.setUnitMode(threadPool.isUnitMode());

			consumer.setConsumerGroup(propertiesConsumer.getConsumerGroup());
			consumer.setConsumeThreadMin(propertiesConsumer.getConsumerThreadMin());
			consumer.setConsumeThreadMax(propertiesConsumer.getConsumerThreadMax());
			consumer.setMaxReconsumeTimes(propertiesConsumer.getMaxReconsumeTimes());
			consumer.setConsumeConcurrentlyMaxSpan(propertiesConsumer.getConsumeConcurrentlyMaxSpan());
			consumer.setPullThresholdForQueue(propertiesConsumer.getPullThresholdForQueue());
			consumer.setPullThresholdSizeForQueue(propertiesConsumer.getPullThresholdSizeForQueue());
			consumer.setPullThresholdForTopic(propertiesConsumer.getPullThresholdForTopic());
			consumer.setPullThresholdSizeForTopic(propertiesConsumer.getPullThresholdSizeForTopic());
			consumer.setPullInterval(propertiesConsumer.getPullInterval());
			consumer.setConsumeMessageBatchMaxSize(propertiesConsumer.getConsumeMessageBatchMaxSize());
			consumer.setPullBatchSize(propertiesConsumer.getPullBatchSize());
			consumer.setPostSubscriptionWhenPull(propertiesConsumer.isPostSubscriptionWhenPull());
			consumer.setMessageModel(propertiesConsumer.getMessageModel());
			consumer.setConsumeFromWhere(propertiesConsumer.getConsumeFromWhere());
			consumer.setConsumeTimestamp(propertiesConsumer.getConsumeTimestamp());
			consumer.setAllocateMessageQueueStrategy(propertiesConsumer.getAllocateMessageQueueStrategy());
			consumer.setMessageListener(propertiesConsumer.getMessageListener());
			consumer.setSuspendCurrentQueueTimeMillis(propertiesConsumer.getSuspendCurrentQueueTimeMillis());
			consumer.setConsumeTimeout(propertiesConsumer.getConsumeTimeout());
			consumer.setAdjustThreadPoolNumsThreshold(propertiesConsumer.getAdjustThreadPoolNumsThreshold());

			return consumer;
		}
		return null;
	}

	/**
	 * todo
	 */
	public DefaultLitePullConsumer defaultLitePullConsumer(){
		return null;
	}


	private void commonProducerConfig(DefaultMQProducer producer){
		producer.setNamesrvAddr(getNameSrvAddr());

		producer.setInstanceName(getInstanceName());
		producer.setUnitName(getUnitName());

		producer.setClientCallbackExecutorThreads(threadPool.getClientCallBackExecutorThreads());

		producer.setNamespace(threadPool.getNameSpace());

		producer.setPollNameServerInterval(threadPool.getPollNameServerInterval());
		producer.setHeartbeatBrokerInterval(threadPool.getHeartbeatBrokerInterval());

		producer.setProducerGroup(threadPool.getProducer().getProducerGroup());
		producer.setDefaultTopicQueueNums(threadPool.getProducer().getDefaultTopicQueueNums());
		producer.setRetryTimesWhenSendAsyncFailed(threadPool.getProducer().getRetryTimesWhenSendAsyncFailed());
		producer.setRetryAnotherBrokerWhenNotStoreOK(threadPool.getProducer().isRetryAnOtherBrokerWhenNotStoreOk());
	}


	private String getUnitName(){
		String unitName = threadPool.getUnitName();
		if(StringUtils.isNotEmpty(unitName)){
			return unitName;
		} else{
			return null;
		}
	}


	private String getInstanceName(){
		String instanceName = threadPool.getInstanceName();
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
		String nameServerAddress = threadPool.getNameServerAddress();
		if(StringUtils.isEmpty(nameServerAddress)){
			throw new IllegalArgumentException("RocketMQ Producer NameServer Address Con't Be NULL");
		}
		return nameServerAddress;
	}


	@Bean
	public ThreadPoolExecutor threadPoolTaskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		RocketMqConfigProperties.ConsumerThreadPool threadPool = this.threadPool.getConsumer().getThreadPool();
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
