package com.example.rabbitmq.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
public class ConsumerApplication{

	public static void main(String[] args){
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
