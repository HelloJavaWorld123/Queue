package com.example.rabbitmq.consumer.config.rabbit;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author : RXK
 * Date : 2020/3/18 11:12
 * Desc: Rabbit Connection Exception Handler
 * 发送到日志平台
 */
@Component
public class RabbitConnectionExceptionHandler implements ExceptionHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConnectionExceptionHandler.class);

	@Override
	public void handleUnexpectedConnectionDriverException(Connection conn, Throwable exception){
		LOGGER.error("--Consumer--Handle Unexpected Connection Driver:{{}},Exception:{}-------",conn,exception);
	}

	@Override
	public void handleReturnListenerException(Channel channel, Throwable exception){
		LOGGER.error("--Consumer---Handle Return Listener:{},Exception:{}------",channel,exception);
	}

	@Override
	public void handleConfirmListenerException(Channel channel, Throwable exception){
		LOGGER.error("---Consumer---Handle Confirm Listener:{},Exception:{}-------",channel,exception);
	}

	@Override
	public void handleBlockedListenerException(Connection connection, Throwable exception){
		LOGGER.error("--Consumer--Handle Blocked Listener:{},Exception:{}------------",connection,exception);
	}

	@Override
	public void handleConsumerException(Channel channel, Throwable exception, Consumer consumer, String consumerTag, String methodName){
		LOGGER.error("--Consumer--Handle Consumer :{},Exception:{},Consumer:{},consumerTag:{},methodName:{}----",channel,exception,consumer,consumerTag,methodName);
	}

	@Override
	public void handleConnectionRecoveryException(Connection conn, Throwable exception){
		LOGGER.error("--Consumer--Handle Connection Recovery:{},Exception:{}-----",conn,exception);
	}

	@Override
	public void handleChannelRecoveryException(Channel ch, Throwable exception){
		LOGGER.error("--Consumer--Handle Channel Recovery:{},Exception:{}----",ch,exception);
	}

	@Override
	public void handleTopologyRecoveryException(Connection conn, Channel ch, TopologyRecoveryException exception){
		LOGGER.error("--Consumer--Handle Topology Recovery:{},Exception:{},TopologyRecoveryException:{}----",conn,exception,exception);
	}
}
