package com.example.rabbitmq.producer.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : RXK
 * Date : 2020/3/31 16:23
 * Desc:
 */
@Configuration
public class ThreadPoolConfig{


	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(20);
		threadPoolTaskExecutor.setMaxPoolSize(50);
		threadPoolTaskExecutor.setQueueCapacity(60);
		threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
		threadPoolTaskExecutor.setThreadNamePrefix("rabbit-producer-");
		threadPoolTaskExecutor.afterPropertiesSet();
		return threadPoolTaskExecutor;
	}

	@Bean
	public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor(){
		return new SimpleAsyncTaskExecutor("rabbit-producer-task");
	}



}
