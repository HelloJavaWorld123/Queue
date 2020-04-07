package com.example.rocketmq.common.util;

import sun.rmi.runtime.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : RXK
 * Date : 2020/4/7 15:57
 * Desc:
 */
public class IpUtils{

	public static String localIp(){
		try{
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostAddress();
		} catch(UnknownHostException e){
			LogUtils.error("获取本地的IP出现异常：",e);
			return null;
		}
	}

	public static String localName(){
		try{
			return InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException e){
			LogUtils.error("获取本地机器的名称出现异常：",e);
			return null;
		}
	}

	public static String ipAndName(){
		return localIp() + "@" + localName();
	}

}
