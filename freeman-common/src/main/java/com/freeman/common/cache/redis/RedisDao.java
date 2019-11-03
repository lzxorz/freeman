package com.freeman.common.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@Component
public class RedisDao {

	@Autowired //@Resource
	private RedisTemplate redisTemplate;

	/** 对redis字符串类型数据操作 */
	@Autowired
	private ValueOperations<String, Object> valueOperations;

	/** 对hash类型的数据操作 */
	@Autowired
	private HashOperations<String, String, Object> hashOperations;

	/** 对链表类型的数据操作 */
	@Autowired
	private ListOperations<String, Object> listOperations;

	/** 对无序集合类型的数据操作 */
	@Autowired
	private SetOperations<String, Object> setOperations;

	/** 对有序集合类型的数据操作 */
	@Autowired
	private ZSetOperations<String, Object> zSetOperations;

	/*public RedisDao() {}
	public RedisDao(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.valueOperations = redisTemplate.opsForValue();
		this.hashOperations = redisTemplate.opsForHash();
		this.listOperations = redisTemplate.opsForList();
		this.setOperations = redisTemplate.opsForSet();
		this.zSetOperations = redisTemplate.opsForZSet();
	}*/





	//=============================common============================
	/**
	 * 指定缓存失效时间
	 * @param key 键
	 * @param time 时间(秒)
	 * @return
	 */
	public Boolean expire(String key, long time) {
		try {
			if (time > 0) {
				//redisTemplate.indexdb.set(indexdb);
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * 指定key在指定的日期过期
	 *
	 * @param key
	 * @param date
	 */
	public void expireKeyAt(String key, Date date) {
		redisTemplate.expireAt(key, date);
	}


	/**
	 * 根据key 获取过期时间
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public Long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 将key设置为永久有效
	 *
	 * @param key
	 */
	public void persistKey(String key) {
		redisTemplate.persist(key);
	}


	/**
	 * 判断key是否存在
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public Boolean hasKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
	 *
	 * @param oldKey
	 * @param newKey
	 */
	public void renameKey(String oldKey, String newKey) {
		redisTemplate.rename(oldKey, newKey);
	}

	/**
	 * newKey不存在时才重命名
	 *
	 * @param oldKey
	 * @param newKey
	 * @return 修改成功返回true
	 */
	public Boolean renameKeyNotExist(String oldKey, String newKey) {
		return redisTemplate.renameIfAbsent(oldKey, newKey);
	}


	/**
	 * 删除缓存
	 * @param key 可以传一个值 或多个
	 */
	@SuppressWarnings("unchecked")
	public void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				redisTemplate.delete(key[0]);
			} else {
				redisTemplate.delete(CollectionUtils.arrayToList(key));
			}
		}
	}

	// ============================String=============================
	/**
	 * 普通缓存获取
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key) {
		return key == null ? null : valueOperations.get(key);
	}

	/**
	 * 普通缓存放入
	 * @param key 键
	 * @param value 值
	 * @return true成功 false失败
	 */
	public Boolean set(String key, Object value) {
		try {
			valueOperations.set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public Boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				valueOperations.set(key, value, time, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 递增
	 * @param key 键
	 * @param delta 要增加几(大于0)
	 * @return
	 */
	public Long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		return valueOperations.increment(key, delta);
	}

	/**
	 * 递减
	 * @param key 键
	 * @param delta  要减少几(小于0)
	 * @return
	 */
	public Long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于0");
		}
		return valueOperations.increment(key, -delta);
	}

	// ================================Map=================================
	/**
	 * HashGet
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return 值
	 */
	public Object hget(String key, String item) {
		return hashOperations.get(key, item);
	}

	/**
	 * 获取hashKey对应的所有键值
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Map<String, Object> hmget(String key) {
		return hashOperations.entries(key);
	}

	/**
	 * HashSet
	 * 
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public Boolean hmset(String key, Map<String, Object> map) {
		try {
			hashOperations.putAll(key, map);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * HashSet 并设置时间
	 * 
	 * @param key  键
	 * @param map  对应多个键值
	 * @param time 时间(秒)
	 * @return true成功 false失败
	 */
	public Boolean hmset(String key, Map<String, Object> map, long time) {
		try {
			hashOperations.putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * 
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true 成功 false失败
	 */
	public Boolean hset(String key, String item, Object value) {
		try {
			hashOperations.put(key, item, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * 
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public Boolean hset(String key, String item, Object value, long time) {
		try {
			hashOperations.put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除hash表中的值
	 * 
	 * @param key  键 不能为null
	 * @param item 项 可以使多个 不能为null
	 */
	public void hdel(String key, Object... item) {
		hashOperations.delete(key, item);
	}

	/**
	 * 判断hash表中是否有该项的值
	 * 
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return true 存在 false不存在
	 */
	public Boolean hHasKey(String key, String item) {
		return hashOperations.hasKey(key, item);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 * 
	 * @param key  键
	 * @param item 项
	 * @param by   要增加几(大于0)
	 * @return
	 */
	public double hincr(String key, String item, double by) {
		return hashOperations.increment(key, item, by);
	}

	/**
	 * hash递减
	 * 
	 * @param key  键
	 * @param item 项
	 * @param by   要减少记(小于0)
	 * @return
	 */
	public double hdecr(String key, String item, double by) {
		return hashOperations.increment(key, item, -by);
	}

	// ============================set=============================
	/**
	 * 根据key获取Set中的所有值
	 * 
	 * @param key 键
	 * @return
	 */
	public Set<Object> sGet(String key) {
		try {
			return setOperations.members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 * 
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public Boolean sHasKey(String key, Object value) {
		try {
			return setOperations.isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将数据放入set缓存
	 * 
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Long sSet(String key, Object... values) {
		try {
			return setOperations.add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 将set数据放入缓存
	 * 
	 * @param key    键
	 * @param time   时间(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Long sSetAndTime(String key, long time, Object... values) {
		try {
			Long count = setOperations.add(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 获取set缓存的长度
	 * 
	 * @param key 键
	 * @return
	 */
	public Long sGetSetSize(String key) {
		try {
			return setOperations.size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 移除值为value的
	 * 
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数
	 */
	public Long setRemove(String key, Object... values) {
		try {
			Long count = setOperations.remove(key, values);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
	// ===============================list=================================

	/**
	 * 获取list缓存的内容
	 * 
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public List<Object> lGet(String key, long start, long end) {
		try {
			return listOperations.range(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取list缓存的长度
	 * 
	 * @param key 键
	 * @return
	 */
	public Long lGetListSize(String key) {
		try {
			return listOperations.size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 通过索引 获取list中的值
	 * 
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public Object lGetIndex(String key, long index) {
		try {
			return listOperations.index(key, index);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public Boolean lSet(String key, Object value) {
		try {
			listOperations.rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return
	 */
	public Boolean lSet(String key, Object value, long time) {
		try {
			listOperations.rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public Boolean lSet(String key, List<Object> value) {
		try {
			listOperations.rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return
	 */
	public Boolean lSet(String key, List<Object> value, long time) {
		try {
			listOperations.rightPushAll(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据索引修改list中的某条数据
	 * 
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return
	 */
	public Boolean lUpdateIndex(String key, long index, Object value) {
		try {
			listOperations.set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移除N个值为value
	 * 
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数
	 */
	public Long lRemove(String key, long count, Object value) {
		try {
			Long remove = listOperations.remove(key, count, value);
			return remove;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
 
	/*public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool(null,"localhost",6379,100,"123456");
		Jedis jedis = jedisPool.getResource();
		//r.get("", RedisConstants.datebase4);
		jedis.select(RedisConstants.datebase4);
		Set<String> str =  jedis.keys("*");
		for (String string : str) {
			System.out.println(string);
		}
	}*/
}
