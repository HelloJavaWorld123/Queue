package com.example.rocketmq.producer.listener.impl;

import com.example.rocketmq.producer.listener.TransactionMessageListenerProcessor;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.springframework.stereotype.Service;

/**
 * @author : RXK
 * Date : 2020/4/7 15:01
 * Desc:
 */
@Service
public class TestMessageListenerProcessor implements TransactionMessageListenerProcessor {

    /**
     *
     * @param msg
     * @param arg : 是发送消息时附件的对象
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String body = new String(msg.getBody());
        logger(TestMessageListenerProcessor.class).info("接收到事务执行的回调：{},{},{}",msg.toString(),arg,body);
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        logger(TestMessageListenerProcessor.class).info("接收到事务检查的回调：{}",msg.toString());
        String transactionId = msg.getTransactionId();
        return LocalTransactionState.UNKNOW;
    }
}
