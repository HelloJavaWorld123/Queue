package com.example.rocketmq.producer.listener;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : RXK
 * Date : 2020/4/7 15:01
 * Desc:
 */
public interface TransactionMessageListenerProcessor extends TransactionListener {

    @Override
    LocalTransactionState executeLocalTransaction(Message msg, Object arg);

    @Override
    LocalTransactionState checkLocalTransaction(MessageExt msg);


   default Logger logger(Class targetClass){
       return LoggerFactory.getLogger(targetClass);
    }


}
