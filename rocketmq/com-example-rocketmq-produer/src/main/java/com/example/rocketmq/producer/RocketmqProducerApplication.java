package com.example.rocketmq.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class RocketmqProducerApplication{

	public static void main(String[] args){
		SpringApplication.run(RocketmqProducerApplication.class, args);
	}

}
