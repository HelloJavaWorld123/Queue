package com.example.rocketmq.consumer.listener;

import com.example.rocketmq.common.util.LogUtils;
import com.example.rocketmqautoconfig.annon.RocketMqListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.filter.ExpressionType;
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
        consumerGroup = "TEST_CUSTOMER_TOPIC_SQL_GROUP_THREE",
        topic = "TEST_CUSTOMER_TOPIC_SQL_TWO",
        selectExpressionType = ExpressionType.SQL92,
        expression = "a == 1 AND c < 100"
)
public class RocketMqGroupFourListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        LogUtils.info(msgs.toString());
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
