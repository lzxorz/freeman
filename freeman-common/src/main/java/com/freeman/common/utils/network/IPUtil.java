package com.freeman.common.utils.network;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

@Slf4j
public class IPUtil extends IPAddressUtil {
	private static final String UNKNOWN = "unknown";
	/**
	 * 获取客户端IP地址
	 * 使用反向代理软件， 则不能通过 request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
	 * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		if (request == null) {
			return UNKNOWN;
		}

		String ip = request.getHeader("X-Forwarded-For"); // 多次反向代理后会有多个ip值，第一个ip才是真实ip
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return StrUtil.split(ip, ",")[0];
	}
	
	/** 判断是否为本地地址 */
	public static boolean isLocalAddr(String ip){
		return StrUtil.containsAny(ip, "127.0.0.1", "0:0:0:0:0:0:0:1");
	}

	/**
	 * 判断是否为内网IP地址
	 * 
	 * tcp/ip协议中保留了三个IP地址区域作为私有地址，其地址范围如下
	 * 10.0.0.0/8：10.0.0.0～10.255.255.255
	 * 172.16.0.0/12：172.16.0.0～172.31.255.255
	 * 192.168.0.0/16：192.168.0.0～192.168.255.255
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isInternalAddr(String ip) {
		if (isLocalAddr(ip)){
			return true;
		}
		
		byte[] addr = textToNumericFormatV4(ip);
		//return internalIp(addr);
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		//10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		//172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		//192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
			case SECTION_1:
				return true;
			case SECTION_2:
				if (b1 >= SECTION_3 && b1 <= SECTION_4) {
					return true;
				}
			case SECTION_5:
				switch (b1) {
					case SECTION_6:
						return true;
				}
			default:
				return false;
		}
	}

	public static String getCityInfo(int algorithm, String ip) {
		try {
			String dbPath = IPUtil.class.getResource("/ip2region/ip2region.db").getPath();
			File file = new File(dbPath);
			if (!file.exists()) {
				file = new File(System.getProperties().getProperty("java.io.tmpdir") + "ip.db");
				IoUtil.copy(IPUtil.class.getClassLoader().getResourceAsStream("classpath:ip2region/ip2region.db"), new FileOutputStream(file));
			}
			DbConfig config = new DbConfig();
			DbSearcher searcher = new DbSearcher(config, file.getPath());
			Method method;
			switch (algorithm) {
				case DbSearcher.BTREE_ALGORITHM:
					method = searcher.getClass().getMethod("btreeSearch", String.class);
					break;
				case DbSearcher.BINARY_ALGORITHM:
					method = searcher.getClass().getMethod("binarySearch", String.class);
					break;
				case DbSearcher.MEMORY_ALGORITYM:
					method = searcher.getClass().getMethod("memorySearch", String.class);
					break;
				default:
					method = searcher.getClass().getMethod("memorySearch", String.class);
					break;
			}
			if (!Util.isIpAddress(ip)) {
				log.error("Error: Invalid ip address");
			}
			DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
			return dataBlock.getRegion();
		} catch (Exception e) {
			log.error("获取地址信息异常：{}", e.getMessage());
		}
		return "";
	}

}
