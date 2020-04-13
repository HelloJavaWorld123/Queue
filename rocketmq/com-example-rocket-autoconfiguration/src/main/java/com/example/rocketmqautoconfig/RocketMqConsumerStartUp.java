package com.example.rocketmqautoconfig;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.annon.RocketMqListener;
import com.example.rocketmqautoconfig.properties.RocketMqConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.filter.ExpressionType;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : RXK
 * Date : 2020/4/9 12:00
 * Desc: 消费自动启动
 * <p>
 * 必须有启动消费者的设置
 * spring.rocketmq.consumer.enable = true
 */
@Component
@EnableConfigurationProperties(value = {RocketMqConfigProperties.class})
public class RocketMqConsumerStartUp implements SmartLifecycle {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RocketMqConfigProperties rocketMqConfigProperties;


    private volatile boolean start = false;


    @Override
    public void start() {
        if (!start) {
            if (rocketMqConfigProperties.getConsumer().isEnable()) {
                startConsumer();
            }
        }
    }

    @Override
    public void stop() {
        if (!start) {
            //TODO
            start = false;
        }
    }

    @Override
    public boolean isRunning() {
        return start;
    }

    @Override
    public boolean isAutoStartup() {
        return Boolean.TRUE;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }


    private void startConsumer() {
        Map<String, Object> RocketMqListeners = applicationContext.getBeansWithAnnotation(RocketMqListener.class).entrySet().stream().filter(item -> !ScopedProxyUtils.isScopedTarget(item.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (RocketMqListeners.isEmpty()) {
            throw new IllegalArgumentException("Rocket MQ Listeners Must Not Be Null");
        }

        RocketMqListeners.forEach(this::configAndRegister);
    }

    private void configAndRegister(String beanName, Object rocketMqListenerBean) {
        DefaultMQPushConsumer consumer = configNewConsumer();
        subscribeTopic(consumer, rocketMqListenerBean);
    }

    private void subscribeTopic(DefaultMQPushConsumer consumer, Object rocketMqListenerBean) {
        RocketMqListener annotation = rocketMqListenerBean.getClass().getAnnotation(RocketMqListener.class);
        if (StringUtils.isAllEmpty(annotation.consumerGroup(), annotation.topic())) {
            throw new IllegalArgumentException("RocketMq Listener ConsumerGroup And Topic must not null");
        }

        String consumerGroup = annotation.consumerGroup();
        String topic = annotation.topic();
        boolean listenerIsEnable = (boolean) rocketMqConfigProperties.getConsumer().getConsumerInfo().getOrDefault(consumerGroup, Collections.EMPTY_MAP).getOrDefault(topic, true);
        LogUtils.info("consumerGroup={" + consumerGroup + "},topic={" + topic + "} isEnable={" + listenerIsEnable + "}");
        if (!listenerIsEnable) {
            return;
        }

        if (MessageListenerConcurrently.class.isAssignableFrom(rocketMqListenerBean.getClass())) {
            consumer.setMessageListener((MessageListenerConcurrently) rocketMqListenerBean);
        } else if (MessageListenerOrderly.class.isAssignableFrom(rocketMqListenerBean.getClass())) {
            consumer.setMessageListener((MessageListenerOrderly) rocketMqListenerBean);
        } else {
            throw new IllegalArgumentException("Message Listener is not MessageListenerConcurrently Or MessageListenerOrderly");
        }


        MessageSelector selector;
        if (ExpressionType.TAG.equals(annotation.selectExpressionType())) {
            selector = MessageSelector.byTag(annotation.expression());
        } else if (ExpressionType.SQL92.equals(annotation.selectExpressionType())) {
            selector = MessageSelector.bySql(annotation.expression());
        } else {
            throw new IllegalArgumentException("Rocket Mq Listener selectExpressType error");
        }

        try {
            consumer.subscribe(annotation.topic(), selector);
            LogUtils.info("订阅:consumerGroup={" + annotation.consumerGroup() + "},topic={" + annotation.topic() + "},selectType={" + annotation.selectExpressionType() + "},express={" + annotation.expression() + "}");
        } catch (MQClientException e) {
            LogUtils.error("订阅消费者出现异常:{}", e);
        }

        consumerStart(consumer);
    }

    private void consumerStart(DefaultMQPushConsumer consumer) {
        try {
            LogUtils.info("consumer 开始启动");
            consumer.start();
            LogUtils.info("consumer 启动成功");
        } catch (MQClientException e) {
            LogUtils.error("启动consumer 失败:{}", e);
        }
    }

    private DefaultMQPushConsumer configNewConsumer() {
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
        //平均分发消息
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());
        consumer.setMessageListener(propertiesConsumer.getMessageListener());
        consumer.setSuspendCurrentQueueTimeMillis(propertiesConsumer.getSuspendCurrentQueueTimeMillis());
        consumer.setConsumeTimeout(propertiesConsumer.getConsumeTimeout());
        consumer.setAdjustThreadPoolNumsThreshold(propertiesConsumer.getAdjustThreadPoolNumsThreshold());

        return consumer;
    }
}
