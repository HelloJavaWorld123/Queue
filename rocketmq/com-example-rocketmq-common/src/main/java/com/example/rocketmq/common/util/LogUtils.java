package com.example.rocketmq.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : RXK
 * Date : 2020/4/7 15:59
 * Desc:
 */
public class LogUtils{

	public static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

	public static void error(String message,Throwable throwable){
		LOGGER.error("-----------------{"+message+":{}}------------------",throwable);
	}

	public static void info(String message){
		LOGGER.info("-----------{"+message+"}-------------");
	}

}
