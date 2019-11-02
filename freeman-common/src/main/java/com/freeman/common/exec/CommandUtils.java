/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.freeman.common.exec;

import java.io.*;

/**
 * Command
 * @author ThinkGem
 * @version 2017年2月17日
 */
public class CommandUtils {

	public static String execute(String command) throws IOException {
		return execute(command, "GBK");
	}
	
	public static String execute(String command, String charsetName) throws IOException {
		Process process = Runtime.getRuntime().exec(command);
		// 记录dos命令的返回信息
		StringBuffer stringBuffer = new StringBuffer();
		// 获取返回信息的流
		InputStream in = process.getInputStream();
		Reader reader = new InputStreamReader(in, charsetName);
		BufferedReader bReader = new BufferedReader(reader);
		String res = bReader.readLine();
		while (res != null) {
			stringBuffer.append(res);
			stringBuffer.append("\n");
			res = bReader.readLine();
		}
		bReader.close();
		reader.close();
		return stringBuffer.toString();
	}

	public static void main(String[] args) {
		try {
			System.out.println(execute("pwd","UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
