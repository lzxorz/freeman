package com.freeman.common.cache.redis;


import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;
import java.util.function.Function;

/**
 * Redis 工具类
 */
public class JedisDao /*implements IRedisManager*/ {
    private static Logger logger = LoggerFactory.getLogger(JedisDao.class);

    private JedisPool jedisPool;

    private static String separator = System.getProperty("line.separator");

    private static final String NX = "NX";   // key 不存在, 才执行 set操作
    private static final String XX = "XX";   // key 存在  , 才执行 set操作

    private static final String SECONDS = "EX";      // 过期时长的单位: 秒
    private static final String MILLISECONDS = "PX"; // 过期时长的单位: 毫秒


    public JedisDao(JedisPool jedisPool) { this.jedisPool = jedisPool; }

    /** 执行命令 */
    private <T> T execCommand(Function<Jedis, T> f) {
        try (Jedis jedis = jedisPool.getResource()) {
            //jedis.select(indexdb);
            return f.apply(jedis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==============================通用========================== //
    public Jedis getJedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断key是否存在
     * @return 返回 true OR false
     */
    public Boolean exists(String key) {
        return execCommand(j -> j.exists(key));
    }

    /**
     * 删除指定的key
     * @param key 一个key 也可以是 string 数组
     * @return 返回删除成功的个数
     */
    public Long del(String... key) {
        return execCommand(j -> j.del(key));
    }

    //@Override
    public void del(byte[] key) { execCommand(j -> j.del(key)); }

    /**
     * 以秒为单位，返回给定 key 的剩余过期时间
     * @return 当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以秒为单位，返回 key 的剩余生存时间。 发生异常 返回 0
     */
    public Long ttl(String key) {
        return execCommand(j -> j.ttl(key));
    }
    
    public Long pttl(String key) { // 毫秒
        return execCommand(j -> j.pttl(key));
    }

    /**
     * 移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )
     * @param key
     * @return 当生存时间移除成功时，返回 1 .如果 key 不存在或 key 没有设置生存时间，返回 0
     */
    public Long persist(String key) {
        return execCommand(j -> j.pttl(key));
    }

    /**
     * 给key设置过期时间
     * @param key
     * @param seconds 时间单位: 秒
     * @return 成功返回1
     */
    public Long expire(String key, int seconds) {
        return execCommand(j -> {
            Long result = null;
            if (seconds != 0) {
                result = j.expire(key, seconds);
            }
            return result;
        });
    }
    
    public Long expire(byte[] key, int seconds) {
        return execCommand(j -> {
            if (seconds != 0) {
                return j.expire(key, seconds);
            }
            return 0L;
        });
    }

    
    public Long pexpire(String key, long milliseconds) { // 时间单位: 毫秒
        return execCommand(j -> {
            Long result = null;
            if (milliseconds != 0) {
                result = j.pexpire(key, milliseconds);
            }
            return result;
        });
    }

    /**
     * 返回满足pattern表达式的所有key
     * keys(*) 返回所有的key
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return execCommand(j -> j.keys(pattern));
    }

    //@Override
    public Set<byte[]> keys(byte[] pattern) { return execCommand(j -> j.keys(pattern)); }

    /**
     * 通过key判断值得类型
     * @param key
     * @return
     */
    public String type(String key) {
        return execCommand(j -> j.type(key));
    }

    /**
     * 清空当前数据库中的所有 key,此命令从不失败。
     * @return 总是返回 SUCCESS
     */
    public String flushDB() {
        return execCommand(j -> j.flushDB());
    }

    // ======================string======================== //
    
    /**  通过key获取String缓存的value */
    public String get(String key) {
        return execCommand(j -> {
            String value = null;
            if (j.exists(key)) {
                value = j.get(key);
                value = StrUtil.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
            return value;
        });
    }
    //@Override
    public byte[] get(byte[] key) { return execCommand(j -> j.get(key)); }


    /**
     * 通过批量的key获取批量的value
     *
     * @param keys 单个key/string数组
     * @return 成功返回value的集合, 失败返回null的集合 ,异常返回空
     */
    public List<String> mget(String... keys) {
        return execCommand(j -> {
            List<String> result = j.mget(keys);
            return result;
        });
    }

    /**
     * 设置缓存
     * 如果key已经存在 则覆盖
     * @param key 键
     * @param value 值
     * @return 成功 返回OK
     */
    public String set(String key, String value) { return execCommand(j -> j.set(key, value)); }

    public byte[] set(byte[] key, byte[] value) { return execCommand(j -> j.get(key)); }

    /**
     * 设置缓存
     * 如果key不存在 则执行set
     * @param key 键
     * @param value 值
     * @param seconds 过期时间：秒
     * @return 成功 返回OK
     */
    public String set(String key, String value, int seconds) { return execCommand(j -> j.set(key, value, NX,SECONDS,seconds)); }

    //@Override
    public byte[] set(byte[] key, byte[] value, int seconds) {
        return execCommand(j -> {
            String result = j.set(key, value);
            expire(key, seconds);
            return result.getBytes();
        });
    }


    /**
     * 批量的设置key:value,可以一个
     * example:
     * obj.mset(new String[]{"key2","value1","key2","value2"})
     *
     * @param keysvalues
     * @return 成功返回OK 失败 异常 返回 null
     *
     */
    public String mset(String... keysvalues) {
        return execCommand(j -> j.mset(keysvalues));
    }

    /**
     * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
     * example:
     * obj.msetnx(new String[]{"key2","value1","key2","value2"})
     *
     * @param keysvalues
     * @return 成功返回1 失败返回0
     */
    @Deprecated
    public Long msetnx(String... keysvalues) {
        return execCommand(j -> j.msetnx(keysvalues));
    }

    /**
     * 新增key,并将 key 的生存时间 (以秒为单位)
     *
     * @param key
     * @param seconds 生存时间 单位：秒
     * @param value
     * @return 设置成功时返回 SUCCESS 。当 seconds 参数不合法时，返回一个错误。
     */
    @Deprecated
    public String setex(String key, String value, int seconds) {
        return execCommand(j -> j.setex(key, seconds, value));
    }

    /**
     * 设置key value, 如果不存在这个key
     * @param key
     * @param value
     * @return 成功返回1 如果存在 返回 0
     */
    @Deprecated
    public Long setnx(String key, String value) {
        return execCommand(j -> {
            Long result = j.setnx(key, value);
            return result;
        });
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
     * 当 key 存在但不是字符串类型时，返回一个错误。
     * @return 返回给定 key 的旧值。当 key 没有旧值时，也即是， key 不存在时，返回 nil
     */
    public String getSet(String key, String value) { return execCommand(j -> j.getSet(key, value)); }

    /**
     * 通过key向指定的value值追加值
     * @param key
     * @param str
     * @return 成功返回 添加后value的长度 失败 返回 添加的 value 的长度 异常返回0L
     */
    public Long append(String key, String str) {
        return execCommand(j -> {
            Long result = j.append(key, str);
            return result;
        });
    }

    /**
     * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
     * @return 加值后的结果
     */
    public Long incr(String key) {
        return execCommand(j -> {
            logger.debug("incr {}", key);
            return j.incr(key);
        });
    }
    /** 通过key给指定的value加值,如果key不存在,则这是value为该值 */
    public Long incrBy(String key, long increment) {
        return execCommand(j -> {
            logger.debug("incrBy {} = {}", key, increment);
            return j.incrBy(key, increment);
        });
    }

    /** 对key的值做减减操作,如果key不存在,则设置key为-1 */
    
    public Long decr(String key) {
        return execCommand(j -> {
            logger.debug("decr {} = {}", key);
            return j.decr(key);
        });
    }
    /** 减去指定的值 */
    public Long decrBy(String key, long decrement) {
        return execCommand(j -> {
            logger.debug("decrBy {} = {}", key, decrement);
            return j.decrBy(key, decrement);
        });
    }

    /**
     * 通过key 和offset 从指定的位置开始将原先value替换
     * 下标从0开始,offset表示从offset下标开始替换
     * 如果替换的字符串长度过小则会这样
     * example:
     *
     * value : bigsea@zto.cn
     * str : abc
     * 从下标7开始替换 则结果为
     * RES : bigsea.abc.cn
     *
     * @param key
     * @param str
     * @param offset
     *            下标位置
     * @return 返回替换后 value 的长度
     */
    public Long setrange(String key, String str, int offset) {
        return execCommand(j -> {
            logger.debug("setrange {} {} {}", key, offset, str);
            return j.setrange(key, offset, str);
        });
    }

    /**
     * 通过下标 和key 获取指定下标位置的 value
     *
     * @param key
     * @param startOffset
     *            开始位置 从0 开始 负数表示从右边开始截取
     * @param endOffset
     * @return 如果没有返回null
     */
    public String getrange(String key, int startOffset, int endOffset) {
        return execCommand(j -> {
            String result = j.getrange(key, startOffset, endOffset);
            return result;
        });
    }

    /**
     * 通过key获取value值的长度
     * @return 失败返回null
     */
    public Long strlen(String key) {
        return execCommand(j -> {
            Long result = j.strlen(key);
            return result;
        });
    }

    // ====================Hash==================== //
    /**
     * 通过key 和 field 获取指定的 value
     *
     * @param key  键 不能为null
     * @param field 项 不能为null
     * @return 值 没有返回null
     */
    public String hget(String key, String field) {
        return execCommand(j -> {
            String value = null;
            value = j.hget(key, field);
            logger.debug("hget {} = {}", key, value);
            return value;
        });
    }

    /**
     * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
     *
     * @param key 一个或多个
     * @return 对应的多个键值
     */
    public Map<String, String> hmget(String key, String... fields) {
        return execCommand(j -> {
            Map<String, String> result = new HashMap();
            if (j.exists(key)) {
                for (String k : fields) {
                    result.put(k, j.hget(key, k));
                }
                logger.debug("hmget {} = {}", key, fields);

            }
            return result;
        });
    }

    /** 通过key获取所有的field和value */
    public Map<String, String> hgetAll(String key) {
        return execCommand(j -> {
            Map<String, String> result = j.hgetAll(key);
            return result;
        });
    }

    /**
     * 通过key给field设置指定的值,如果key不存在,则先创建
     *
     * @param key
     * @param field 字段
     * @param value
     * @return 如果存在返回0 异常返回null
     */
    public Long hset(String key, String field, String value) {
        return execCommand(j -> {
            Long result = j.hset(key, field, value);
            logger.debug("hget {} = {}", key, value);
            return result;
        });
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param field  项
     * @param value 值
     * @param milliscends  时间(毫秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public Boolean hset(String key, String field, String value, long milliscends) {
        return execCommand(j -> {
            Long result = j.hset(key, field, value);
            pexpire(key, milliscends);
            logger.debug("hget {} = {}", key, value);
            return result>-1;
        });
    }

    /**
     * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    @Deprecated
    public Long hsetnx(String key, String field, String value) {
        return execCommand(j -> {
            Long result = j.hsetnx(key, field, value);
            logger.debug("hget {} = {}", key, value);
            return result;
        });
    }

    /**
     * 通过key同时设置 hash的多个field
     *
     * @param key 键
     * @param map 对应多个键值
     * @return 成功返回OK 失败/异常返回null
     */
    public String hmset(String key, Map<String, String> map) {
        return execCommand(j -> {
            String result = j.hmset(key, map);
            logger.debug("hmset {} = {}", key, map);
            return result;
        });
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param scends 时间(秒)
     * @return true成功 false失败
     */
    public Boolean hmset(String key, Map<String, String> map, int scends) {
        return execCommand(j -> {
            String result = j.hmset(key, map);
            expire(key, scends);
            logger.debug("hmset {} = {}", key, map);
            return result.equals("+SUCCESS\r\n");
        });
    }


    /**
     * 通过key 删除指定的 field
     *
     * @param key  键 不能为null
     * @param fields 可以是一个field也可以是一个数组 不能为null
     */
    public Long hdel(String key, String... fields) {
        return execCommand(j -> {
            Long result = j.hdel(key, fields);
            logger.debug("hdel {} = {}", key, fields);
            return result;
        });
    }

    /**
     * 通过key和field判断是否有指定的value存在
     *
     * @param key  键 不能为null
     * @param field 项 不能为null
     * @return true 存在 false不存在
     */
    public Boolean hexists(String key, String field) {
        return execCommand(j -> {
            logger.debug("hexists {} = {}", key, field);
            return j.hexists(key, field);
        });
    }

    /**
     * 通过key给指定的field的value加上给定的值
     * 如果不存在,就会创建一个 并把新增后的值返回
     *      * 为哈希表 key 中的域 field 的值加上增量 increment 。增量也可以为负数，相当于对给定域进行减法操作。 如果 key
     *      * 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
     *      * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。本操作的值被限制在 64 位(bit)有符号数字表示之内。
     *      * 将名称为key的hash中field的value增加integer
     * @param key  键
     * @param field 项
     * @param increment 要增加的整数(传负数为递减)
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field的值。异常返回0
     */
    public Long hincrBy(String key, String field, long increment) {
        return execCommand(j -> {
            logger.debug("hincrBy {} {} {}", key,field,increment);
            return j.hincrBy(key,field,increment);
        });
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param field 项
     * @param increment 要增加的浮点数(传负数为递减)
     * @return
     */
    public Double hincrBy(String key, String field, double increment) {
        return execCommand(j -> {
            logger.debug("hincrBy {} {} {}", key,field,increment);
            return j.hincrByFloat(key,field,increment);
        });
    }

    /** 通过key返回field的数量 */
    public Long hlen(String key) {
        return execCommand(j -> {
            logger.debug("hlen {}", key);
            return j.hlen(key);
        });
    }

    /** 通过key返回所有的field */
    public Set<String> hkeys(String key) {
        return execCommand(j -> {
            logger.debug("hkeys {}", key);
            return j.hkeys(key);
        });
    }

    /** 通过key返回所有和key有关的value */
    public List<String> hvals(String key) {
        return execCommand(j -> {
            logger.debug("hvals {}", key);
            return j.hvals(key);
        });
    }

    // =============================list============================ //

    /**
     * 通过key向list头部添加字符串
     * @param key
     * @param strs 可以是一个string 也可以是string数组
     * @return 返回list的value个数
     */
    public Long lpush(String key, String... strs) {
        return execCommand(j -> j.lpush(key, strs));
    }

    /**
     * 通过key向list尾部添加字符串
     * @param key
     * @param strs 可以是一个string 也可以是string数组
     * @return 返回list的value个数
     */
    public Long rpush(String key, String... strs) {
        return execCommand(j -> j.rpush(key, strs));
    }

    /**
     * 通过key在list指定的位置之前或者之后 添加字符串元素
     *
     * @param key
     * @param where
     *            LIST_POSITION枚举类型
     * @param pivot
     *            list里面的value
     * @param value
     *            添加的value
     * @return
     */
    public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        return execCommand(j -> j.linsert(key, where, pivot, value));
    }

    /**
     * 通过key设置list指定下标位置的value
     * 如果下标超过list里面value的个数则报错
     *
     * @param key
     * @param index 从0开始
     * @param value
     * @return 成功返回OK
     */
    public String lset(String key, Long index, String value) {
        return execCommand(j -> j.lset(key, index, value));
    }

    /**
     * 通过key从对应的list中删除指定的count个 和 value相同的元素
     * @param key
     * @param count 当count为0时删除全部
     * @param value
     * @return 返回被删除的个数
     */
    public Long lrem(String key, long count, String value) {
        return execCommand(j -> j.lrem(key, count, value));
    }

    /**
     * 通过key保留list中从strat下标开始到end下标结束的value值
     *
     * @param key
     * @param start
     * @param end
     * @return 成功返回OK
     */
    public String ltrim(String key, long start, long end) {
        return execCommand(j -> j.ltrim(key, start, end));
    }

    /**
     * 通过key从list的头部删除一个value,并返回该value
     *
     * @param key
     * @return
     */
    
    synchronized public String lpop(String key) {
        return execCommand(j -> j.lpop(key));
    }

    /**
     * 通过key从list尾部删除一个value,并返回该元素
     *
     * @param key
     * @return
     */
    
    synchronized public String rpop(String key) {
        return execCommand(j -> j.rpop(key));
    }

    /**
     * 通过key从一个list的尾部删除一个value并添加到另一个list的头部,并返回该value
     * 如果第一个list为空或者不存在则返回null
     *
     * @param srckey
     * @param dstkey
     * @return
     */
    public String rpoplpush(String srckey, String dstkey) {
        return execCommand(j -> j.rpoplpush(srckey, dstkey));
    }

    /**
     * 通过key获取list中指定下标位置的value
     *
     * @param key
     * @param index
     * @return 如果没有返回null
     */
    public String lindex(String key, long index) {
        return execCommand(j -> j.lindex(key, index));
    }

    /**
     * 通过key返回list的长度
     *
     * @param key
     * @return
     */
    public Long llen(String key) {
        return execCommand(j -> j.llen(key));
    }

    /**
     * 通过key获取list指定下标位置的value
     * 如果start 为 0 end 为 -1 则返回全部的list中的value
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        return execCommand(j -> j.lrange(key, start, end));
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value
     *
     * @param key
     * @param index
     * @param value
     * @return 操作成功返回 ok ，否则返回错误信息
     */
    public String lset(String key, long index, String value) {
        return execCommand(j -> j.lset(key, index, value));
    }

    /**
     * 返回给定排序后的结果
     *
     * @param key
     * @param sortingParameters
     * @return 返回列表形式的排序结果
     */
    public List<String> sort(String key, SortingParams sortingParameters) {
        return execCommand(j -> j.sort(key, sortingParameters));
    }

    /**
     * 返回排序后的结果，排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
     *
     * @param key
     * @return 返回列表形式的排序结果
     */
    public List<String> sort(String key) {
        return execCommand(j -> j.sort(key));
    }

    // ============================set================================= //
    /**
     * 通过key向指定的set中添加value
     *
     * @param key
     * @param members
     *            可以是一个String 也可以是一个String数组
     * @return 添加成功的个数
     */
    public Long sadd(String key, String... members) {
        return execCommand(j -> j.sadd(key, members));
    }

    /**
     * 通过key删除set中对应的value值
     *
     * @param key
     * @param members
     *            可以是一个String 也可以是一个String数组
     * @return 删除的个数
     */
    public Long srem(String key, String... members) {
        return execCommand(j -> j.srem(key, members));
    }

    /**
     * 通过key随机删除一个set中的value并返回该值
     *
     * @param key
     * @return
     */
    public String spop(String key) {
        return execCommand(j -> j.spop(key));
    }

    /**
     * 通过key获取set中的差集
     * 以第一个set为标准
     *
     * @param keys
     *            可以是一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public Set<String> sdiff(String... keys) {
        return execCommand(j -> j.sdiff(keys));
    }

    /**
     * 通过key获取set中的差集并存入到另一个key中
     * 以第一个set为标准
     *
     * @param dstkey
     *            差集存入的key
     * @param keys
     *            可以是一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public Long sdiffstore(String dstkey, String... keys) {
        return execCommand(j -> j.sdiffstore(dstkey, keys));
    }

    /**
     * 通过key获取指定set中的交集
     *
     * @param keys
     *            可以是一个string 也可以是一个string数组
     * @return
     */
    public Set<String> sinter(String... keys) {
        return execCommand(j -> j.sinter(keys));
    }

    /**
     * 通过key获取指定set中的交集 并将结果存入新的set中
     *
     * @param dstkey
     * @param keys
     *            可以是一个string 也可以是一个string数组
     * @return
     */
    public Long sinterstore(String dstkey, String... keys) {
        return execCommand(j -> j.sinterstore(dstkey, keys));
    }

    /**
     * 通过key返回所有set的并集
     *
     * @param keys
     *            可以是一个string 也可以是一个string数组
     * @return
     */
    public Set<String> sunion(String... keys) {
        return execCommand(j -> j.sunion(keys));
    }

    /**
     * 通过key返回所有set的并集,并存入到新的set中
     *
     * @param dstkey
     * @param keys
     *            可以是一个string 也可以是一个string数组
     * @return
     */
    public Long sunionstore(String dstkey, String... keys) {
        return execCommand(j -> j.sunionstore(dstkey, keys));
    }

    /**
     * 通过key将set中的value移除并添加到第二个set中
     *
     * @param srckey
     *            需要移除的
     * @param dstkey
     *            添加的
     * @param member
     *            set中的value
     * @return
     */
    public Long smove(String srckey, String dstkey, String member) {
        return execCommand(j -> j.smove(srckey, dstkey, member));
    }

    /**
     * 通过key获取set中value的个数
     *
     * @param key
     * @return
     */
    public Long scard(String key) {
        return execCommand(j -> j.scard(key));
    }

    /**
     * 通过key判断value是否是set中的元素
     *
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(String key, String member) {
        return execCommand(j -> j.sismember(key, member));
    }

    /**
     * 通过key获取set中随机的value,不删除元素
     *
     * @param key
     * @return
     */
    public String srandmember(String key) {
        return execCommand(j -> j.srandmember(key));
    }

    /**
     * 通过key获取set中所有的value
     *
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        return execCommand(j -> j.smembers(key));
    }

    // ==========================zset=============================== //
    /**
     * 通过key向zset中添加value,score,其中score就是用来排序的
     * 如果该value已经存在则根据score更新元素
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Long zadd(String key, double score, String member) {
        return execCommand(j -> j.zadd(key, score, member));
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。min=0,max=-1代表所有元素
     *
     * @param key
     * @param min
     * @param max
     * @return 指定区间内的有序集成员的列表。
     */
    public Set<String> zrange(String key, long min, long max) {
        return execCommand(j -> j.zrange(key, min, max));
    }

    /**
     * 统计有序集 key 中,值在 min 和 max 之间的成员的数量
     *
     * @param key
     * @param min
     * @param max
     * @return 值在 min 和 max 之间的成员的数量。异常返回0
     */
    public Long zcount(String key, double min, double max) {
        return execCommand(j -> j.zcount(key, min, max));
    }

    /**
     * 通过key增加该zset中value的score的值
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Double zincrby(String key, double score, String member) {
        return execCommand(j -> j.zincrby(key, score, member));
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从小到大排序
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrank(String key, String member) {
        return execCommand(j -> j.zrank(key, member));
    }

    /**
     * 通过key返回指定score内zset中的value
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zrangeByScore(String key, String min, String max) {
        return execCommand(j -> j.zrangeByScore(key, min, max));
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从大到小排序
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(String key, String member) {
       return execCommand(j -> j.zrevrank(key, member));
    }

    /**
     * 通过key将获取score从start到end中zset的value
     * socre从大到小排序
     * 当start为0 end为-1时返回全部
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end) {
        return execCommand(j -> j.zrevrange(key, start, end));
    }

    /**
     * 通过key返回指定score内zset中的value
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return execCommand(j -> j.zrevrangeByScore(key, max, min));
    }
    
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return execCommand(j -> j.zrevrangeByScore(key, max, min));
    }

    /**
     * 返回指定区间内zset中value的数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zcount(String key, String min, String max) {
        return execCommand(j -> j.zcount(key, min, max));
    }

    /**
     * 通过key返回zset中的value个数
     *
     * @param key
     * @return
     */
    public Long zcard(String key) {
        return execCommand(j -> j.zcard(key));
    }

    /**
     * 通过key获取zset中value的score值
     *
     * @param key
     * @param member
     * @return
     */
    public Double zscore(String key, String member) {
        return execCommand(j -> j.zscore(key, member));
    }

    /**
     * 通过key删除在zset中指定的value
     * @param key
     * @param members 可以是一个string 也可以是一个string数组
     * @return
     */
    public Long zrem(String key, String... members) {
        return execCommand(j -> j.zrem(key, members));
    }

    /** 通过key删除给定区间内的元素 */
    public Long zremrangeByRank(String key, long start, long end) {
        return execCommand(j -> j.zremrangeByRank(key, start, end));
    }

    /** 通过key删除指定score内的元素 */
    public Long zremrangeByScore(String key, double start, double end) {
        return execCommand(j -> j.zremrangeByScore(key, start, end));
    }

    
    public Long zremrangeByScore(String key, String start, String end) {
        return execCommand(j -> j.zremrangeByScore(key, start, end));
    }


    // ===========================JWT========================== //

    /**
     * 登录/JWT过期后 尝试刷新
     * 在线jwt redis hash缓存 key: 前缀+用户ID, field: 平台标识(名称), value: 令牌
     * @param hashKey loginJwtPrefix:用户ID (例如： freeman:jwt:1)
     * @param platform 登录平台标识 (例如： PC)
     * @param oldJwt 旧JWT
     * @param newJwt 新JWT
     * @return SUCCESS:成功, NO:失败
     */
    public boolean tryRefreshToken(String hashKey, String platform, String oldJwt, String newJwt) {
        //String luaScript = "";
        return "SUCCESS".equals(execCommand(j -> j.eval(
                "-- 检查hash key field (前缀+用户ID,登录平台）\n" +
                "local token = redis.call('hget',KEYS[1],KEYS[2])\n" +
                "-- 判断取到的token为空 或者 令牌不是两个, 返回'FAIL' \n" +
                "if(token == nil or token == '') then return 'FAIL' end\n" +
                "if(2~=#ARGV) then return 'FAIL' end\n" +
                "-- 判断取到的token与传参的旧令牌 不一致,就不能更新令牌, 返回'FAIL' \n" +
                "if(token~=ARGV[1]) then return 'FAIL' end\n" +
                "-- 保存新令牌(以旧换新) \n" +
                "redis.call('hset',KEYS[1],KEYS[2],ARGV[2]) \n" +
                "-- 保存平滑过渡令牌 \n" +
                "redis.call('set',ARGV[1],'transition','NX','EX',30) \n" +
                "return 'SUCCESS'", 2, hashKey, platform, oldJwt, newJwt)));
    }




    // ===========================redis监控信息========================== //

    public List<RedisInfo> getRedisInfo() {
        String info = execCommand(
                j -> {
                    Client client = j.getClient();
                    client.info();
                    return client.getBulkReply();
                }
        );
        List<RedisInfo> infoList = new ArrayList<>();
        String[] strs = Objects.requireNonNull(info).split(separator);
        if (strs.length > 0) {
            for (String str1 : strs) {
                String[] str = str1.split(":");
                if (str.length > 1) {
                    infoList.add(new RedisInfo(str[0], str[1]));
                }
            }
        }
        return infoList;
    }

    
    public Map<String, Object> getKeysSize() {
        Long dbSize = execCommand(
                j -> {
                    Client client = j.getClient();
                    client.dbSize();
                    return client.getIntegerReply();
                }
        );
        Map<String, Object> map = new HashMap<>();
        map.put("create_time", System.currentTimeMillis());
        map.put("dbSize", dbSize);
        return map;
    }
    
    public Long dbSize() { return execCommand(j -> j.dbSize()); }

    public Long dbSize(byte[] bytes) {  return execCommand(j -> j.dbSize());}

    public Map<String, Object> getMemoryInfo() {
        String info = execCommand(
                j -> {
                    Client client = j.getClient();
                    client.info();
                    return client.getBulkReply();
                }
        );
        String[] strs = Objects.requireNonNull(info).split(separator);
        Map<String, Object> map = null;
        for (String s : strs) {
            String[] detail = s.split(":");
            if ("used_memory".equals(detail[0])) {
                map = new HashMap<>();
                map.put("used_memory", detail[1].substring(0, detail[1].length() - 1));
                map.put("create_time", System.currentTimeMillis());
                break;
            }
        }
        return map;
    }


}
