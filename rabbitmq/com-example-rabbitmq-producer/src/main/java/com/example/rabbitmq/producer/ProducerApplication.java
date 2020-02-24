package com.example.rabbitmq.producer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

/**
 * @author admin
 *
 */
@EnableRabbit
@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
public class ProducerApplication{

	public static void main(String[] args){
		SpringApplication.run(ProducerApplication.class, args);
	}

}
