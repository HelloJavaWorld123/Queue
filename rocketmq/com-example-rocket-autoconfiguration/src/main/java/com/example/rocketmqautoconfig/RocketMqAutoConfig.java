package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.IpUtils;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : RXK
 * Date : 2020/4/7 15:09
 * Desc: 配置 RocketMq 的 DefaultRocketMqProducer
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.rocketmq",
                       name = "nameServerAddress",
                       matchIfMissing = true,
                       havingValue = "true")
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqAutoConfig{

	private final RocketMqConfigProperties rocketMqConfigProperties;

	public RocketMqAutoConfig(RocketMqConfigProperties rocketMqConfigProperties){
		this.rocketMqConfigProperties = rocketMqConfigProperties;
	}


	@Bean
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "producer.enable",
	                       havingValue = "true",
	                       matchIfMissing = false)
	@ConditionalOnMissingBean(value = DefaultMQProducer.class)
	@SuppressWarnings("uncheck")
	public DefaultMQProducer defaultMqProducer(){
		if(rocketMqConfigProperties.getProducer().isEnable()){
			DefaultMQProducer producer = new DefaultMQProducer();
			commonProducerConfig(producer);
			return producer;
		}
		return null;
	}


	@Bean
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "producer.enable",
	                       matchIfMissing = false)
	@ConditionalOnMissingBean(TransactionMQProducer.class)
	public TransactionMQProducer transactionMqProducer(){
		TransactionMQProducer transactionMqProducer = new TransactionMQProducer();
		commonProducerConfig(transactionMqProducer);


		//事务句柄的参数设置
		transactionMqProducer.setExecutorService(threadPoolTaskExecutor());

		return transactionMqProducer;
	}

	@Bean
	@ConditionalOnProperty(prefix = "spring.rocketmq",value = "consumer.enable",
	                       havingValue = "true",
	                       matchIfMissing = false)
	@ConditionalOnMissingBean(value = DefaultMQPushConsumer.class)
	public DefaultMQPushConsumer defaultMqPushConsumer(){
		if(rocketMqConfigProperties.getConsumer().isEnable()){
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
			RocketMqConfigProperties.Consumer propertiesConsumer = rocketMqConfigProperties.getConsumer();
			consumer.setNamesrvAddr(rocketMqConfigProperties.getNameServerAddress());
			consumer.setNamespace(rocketMqConfigProperties.getNameSpace());
			consumer.setUnitName(rocketMqConfigProperties.getUnitName());
			consumer.setUseTLS(rocketMqConfigProperties.isUseTLS());
			consumer.setLanguage(rocketMqConfigProperties.getLanguageCode());
			consumer.setHeartbeatBrokerInterval(rocketMqConfigProperties.getHeartbeatBrokerInterval());
			consumer.setClientCallbackExecutorThreads(rocketMqConfigProperties.getClientCallBackExecutorThreads());
			consumer.setPollNameServerInterval(rocketMqConfigProperties.getPollNameServerInterval());
			consumer.setPullTimeDelayMillsWhenException(rocketMqConfigProperties.getPullTimeDelayMillsWhenException());
			consumer.setUnitMode(rocketMqConfigProperties.isUnitMode());

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
		executor.setCorePoolSize(rocketMqConfigProperties.getConsumerThreadPool().getCorePoolSize());
		executor.setMaxPoolSize(rocketMqConfigProperties.getConsumerThreadPool().getMaxPoolSize());
		executor.setKeepAliveSeconds(rocketMqConfigProperties.getConsumerThreadPool().getKeepLiveSeconds());
		executor.setRejectedExecutionHandler(rocketMqConfigProperties.getConsumerThreadPool().getRejectedExecutionHandler());
		executor.setWaitForTasksToCompleteOnShutdown(rocketMqConfigProperties.getConsumerThreadPool().isWaitForTasksToCompleteWhenShutDown());
		executor.setAllowCoreThreadTimeOut(rocketMqConfigProperties.getConsumerThreadPool().isAllowCoreThreadTimeOut());
		executor.setAwaitTerminationSeconds(rocketMqConfigProperties.getConsumerThreadPool().getAwaitTerminationForSeconds());
		executor.initialize();
		return executor.getThreadPoolExecutor();
	}

}
