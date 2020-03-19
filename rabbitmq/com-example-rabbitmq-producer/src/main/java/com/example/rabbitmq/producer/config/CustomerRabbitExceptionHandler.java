package com.example.rabbitmq.producer.config;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/19 11:32
 * Desc: 处理RabbitMq在启动时 出现的异常
 * 发送动日志中心进行处理
 */
@Component
public class CustomerRabbitExceptionHandler implements ExceptionHandler{
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRabbitExceptionHandler.class);

	@Override
	public void handleUnexpectedConnectionDriverException(Connection conn, Throwable exception){
		LOGGER.error("---Producer--Handle Unexpected Connection Driver:{{}},Exception:{}-------",conn,exception);
	}

	@Override
	public void handleReturnListenerException(Channel channel, Throwable exception){
		LOGGER.error("--Producer---Handle Return Listener:{},Exception:{}------",channel,exception);
	}

	@Override
	public void handleConfirmListenerException(Channel channel, Throwable exception){
		LOGGER.error("---Producer---Handle Confirm Listener:{},Exception:{}-------",channel,exception);
	}

	@Override
	public void handleBlockedListenerException(Connection connection, Throwable exception){
		LOGGER.error("---Producer---Handle Blocked Listener:{},Exception:{}------------",connection,exception);
	}

	@Override
	public void handleConsumerException(Channel channel, Throwable exception, Consumer consumer, String consumerTag, String methodName){
		LOGGER.error("--Producer--Handle Consumer :{},Exception:{},Consumer:{},consumerTag:{},methodName:{}----",channel,exception,consumer,consumerTag,methodName);
	}

	@Override
	public void handleConnectionRecoveryException(Connection conn, Throwable exception){
		LOGGER.error("--Producer--Handle Connection Recovery:{},Exception:{}-----",conn,exception);
	}

	@Override
	public void handleChannelRecoveryException(Channel ch, Throwable exception){
		LOGGER.error("--Producer--Handle Channel Recovery:{},Exception:{}----",ch,exception);
	}

	@Override
	public void handleTopologyRecoveryException(Connection conn, Channel ch, TopologyRecoveryException exception){
		LOGGER.error("--Producer--Handle Topology Recovery:{},Exception:{},TopologyRecoveryException:{}----",conn,exception,exception);
	}
}
