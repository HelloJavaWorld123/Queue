package com.example.rabbit.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : RXK
 * Date : 2020/3/25 16:41
 * Desc:
 *
 * 地址工具类
 */
public class IPAddressUtils{

	public static String ipAndHostName(){
		return hostName() + ":" + ipAddress();
	}

	public static String hostName(){
		try{
			return InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException e){
			LoggerUtils.error("获取Host Name时 出现异常",e);
			return e.getMessage();
		}
	}

	public static String ipAddress(){
		try{
			return InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException e){
			LoggerUtils.error("获取Ip Address时 出现异常",e);
			return e.getMessage();
		}
	}


}
