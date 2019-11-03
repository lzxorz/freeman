package com.freeman.common.cache;

import com.alibaba.fastjson.JSON;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.common.utils.StrUtil;
import com.freeman.sys.domain.SysDictItem;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 字典工具类 (redis中获取)
 * 项目启动后,加载字典缓存, 增删改都会更新缓存
 * @author 刘志新
 */

public class DictUtil {
	private static JedisDao jedisDao = SpringContextUtil.getBean(JedisDao.class);


	/**
	 * 获取字典数据标签　(单个/多个拼接　a,b,c,d)
	 * @param dictType 字典类型
	 * @param dictValues 字典值字符串(单个/多个拼接　a,b,c,d)
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getDictLabel(String dictType, String dictValues, String defaultValue){
		if (StrUtil.isNotBlank(dictType) && StrUtil.isNotBlank(dictValues)){
			List<SysDictItem> dictList = getDictList(dictType);
			boolean has = false;
			if (!ObjectUtils.isEmpty(dictList)) {
				StringJoiner sj = new StringJoiner(",");
				for (String value : StrUtil.splitTrim(dictValues, ",")) {
					for (SysDictItem dictData : dictList) {
						if (dictData.getValue().equals(value)){
							sj.add(dictData.getLabel());
							has = true;
							break;
						}
					}
					if (!has){
						sj.add("未知");
					}else {
						has = false;
					}
				}
				return sj.toString();
			}
		}
		return defaultValue;
	}


	/**
	 * 获取字典数据值(单个/多个拼接　a,b,c,d)
	 * @param dictType 字典类型
	 * @param dictLabels 字典标签(单个/多个拼接　a,b,c,d)
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getDictValue(String dictType, String dictLabels, String defaultValue) {
		if (StrUtil.isNotBlank(dictType) && StrUtil.isNotBlank(dictLabels)){
			List<SysDictItem> dictList = getDictList(dictType);
			boolean has = false;
			if (!ObjectUtils.isEmpty(dictList)) {
				StringJoiner sj = new StringJoiner(",");
				for (String label : StrUtil.splitTrim(dictLabels, ",")) {
					for (SysDictItem dictData : dictList) {
						if (dictData.getLabel().equals(label)){
							sj.add(dictData.getValue());
							has = true;
							break;
						}
					}
					if (!has){
						sj.add("未知");
					}else {
						has = false;
					}
				}
				return sj.toString();
			}
		}
		return defaultValue;
	}

	/**
	 * 获取字典数据List
	 * @param dictType 字典类型
	 * @return
	 */
	public static List<SysDictItem> getDictList(String dictType){
		String dicts = jedisDao.hget(Constants.CACHE.DICT_PREFIX, dictType);
		if (Objects.nonNull(dicts)){
			return JSON.parseArray(dicts, SysDictItem.class);
		}
		return null;
	}

}
