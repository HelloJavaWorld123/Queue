package com.example.rabbit.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : RXK
 * Date : 2020/3/20 15:12
 * Desc: 日志工具类
 */
public class LoggerUtils{

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtils.class);



	public static void info(String message){
		LOGGER.info(message);
	}

	public static void error(String message,Throwable throwable){
		LOGGER.error(message,throwable);
	}




}
