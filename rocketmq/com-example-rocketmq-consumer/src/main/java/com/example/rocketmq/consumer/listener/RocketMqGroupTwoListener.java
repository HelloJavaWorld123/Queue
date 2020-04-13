package com.example.rocketmq.consumer.listener;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.annon.RocketMqListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : RXK
 * Date : 2020/4/7 15:01
 * Desc:
 */
@Component
@RocketMqListener(
        consumerGroup = "TEST_CUSTOMER_TOPIC_TAG_GROUP_ONE",
        topic = "TEST_CUSTOMER_TOPIC_TAG_TWO",
        expression = "TEST_CUSTOMER_TAG_ONE || TEST_CUSTOMER_TAG_TWO"
)
public class RocketMqGroupTwoListener implements MessageListenerOrderly {
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        LogUtils.info("MessageListenerOrderly 接收到消息 :{"+msgs.toString()+"}");
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
