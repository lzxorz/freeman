/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.freeman.common.utils.excel.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * 金额类型转换（保留两位）
 * @author ThinkGem
 * @version 2015-9-29
 */
public class MoneyConverter implements IConverter {

	/**
	 * 获取对象值（导入）
	 */
	public Object imp(String val) {
		return val == null ? "" : val.replaceAll(",", "");
	}

	/**
	 * 获取对象值（导出）
	 */
	public String exp(Object val) {
		NumberFormat nf = new DecimalFormat(",##0.00"); 
		return val == null ? "" : nf.format(val);
	}


	/**
	 * 获取对象值格式（导出）
	 */
	public static String getDataFormat() {
		return "0.00";
	}
}
