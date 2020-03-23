package com.example.rabbitmq.consumer.config.thread;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : RXK
 * Date : 2020/3/23 18:49
 * Desc: 自定义创建线程池
 */
@Configuration
public class ThreadPoolFactory{


	@Bean
	@ConditionalOnMissingBean(ThreadPoolTaskExecutor.class)
	public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(20);
		executor.setThreadGroupName("consumer-");
		executor.setThreadNamePrefix("rabbit-");
		executor.setKeepAliveSeconds(300);
		executor.setAwaitTerminationSeconds(120);
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
		executor.afterPropertiesSet();
		return executor;
	}



}
