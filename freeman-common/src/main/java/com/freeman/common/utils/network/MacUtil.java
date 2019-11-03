package com.freeman.common.utils.network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取客户端MAC地址工具
 * 作者：码农下的天桥
 * 来源：CSDN
 * 原文：https://blog.csdn.net/cdnight/article/details/86741265
 */
public class MacUtil {

	/***因为一台机器不一定只有一个网卡呀，所以返回的是数组是很合理的**/
	public static List<String> getMacList() throws Exception {
		java.util.Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		StringBuilder sb = new StringBuilder();
		ArrayList<String> tmpMacList=new ArrayList<>();
		while(en.hasMoreElements()){
			NetworkInterface iface = en.nextElement();
			List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
			for(InterfaceAddress addr : addrs) {
				InetAddress ip = addr.getAddress();
				NetworkInterface network = NetworkInterface.getByInetAddress(ip);
				if(network==null){continue;}
				byte[] mac = network.getHardwareAddress();
				if(mac==null){continue;}
				sb.delete( 0, sb.length() );
				for (int i = 0; i < mac.length; i++) {sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));}
				tmpMacList.add(sb.toString());
			}        }
		if(tmpMacList.size()<=0){return tmpMacList;}
		/***去重，别忘了同一个网卡的ipv4,ipv6得到的mac都是一样的，肯定有重复，下面这段代码是。。流式处理**/
		List<String> unique = tmpMacList.stream().distinct().collect(Collectors.toList());
		return unique;
	}
	/*public static void main(String[] args) throws Exception {
		List<String> macs=getMacList();
		System.out.println("本机的mac网卡的地址有："+macs);
	}*/

}
