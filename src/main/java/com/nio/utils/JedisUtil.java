package com.nio.utils;

import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 
 * @author martin
 *
 * 时间  2016年5月14日 下午4:47:08
 *
 * 说明   redis工具类
 */
public class JedisUtil {
	
	private  final static Logger logger =Logger.getLogger(JedisUtil.class);

	private static JedisPool pool;
	
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		pool = new JedisPool(config, PropertyUtils.REDIS_HOST, PropertyUtils.REDIS_PORT,
				Protocol.DEFAULT_TIMEOUT, PropertyUtils.REDIS_AUTH);
	}
	
	public static Jedis getResource() {
		return pool.getResource();
	}
	
	public static void returnResource(Jedis jedis) {
		jedis.close();
	}
	
	public static void del(String key){
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.del(key);
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
	}
	
	public static long lpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long n = jedis.lpush(key, value);
			logger.info("lpush: " + key + " - " + value);
			return n;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
	
	public static String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			String msg = jedis.rpop(key);
			logger.info("rpop: " + key + " - " + msg);
			return msg;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return null;
	}
	
	public static long sadd(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long n = jedis.sadd(key, value);
			logger.info("sadd: " + key + " - " + value);
			return n;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
	
	public static long srem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long n = jedis.srem(key, value);
			logger.info("srem: " + key + " - " + value);
			return n;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
	
	public static Set<String> snumbers(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			Set<String> mems = jedis.smembers(key);
			logger.info("smem: " + key + " - " + (mems==null?0:mems.size()));
			return mems;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return null;
	}
	
	public static String hget(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			String str = jedis.hget(key, field);
			logger.info("hget: " + key + " - " + field + " - " + str);
			return str;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return null;
	}
	
	public static long hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long l = jedis.hset(key, field, value);
			logger.info("hset: " + key + " - " + field + " - " + value);
			return l;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
	
	public static long hdel(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long l = jedis.hdel(key, field);
			logger.info("hdel: " + key + " - " + field);
			return l;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
	
	public static Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			Map<String, String> map = jedis.hgetAll(key);
			logger.info("hgetAll: " + key + " - " + (map==null?0:map.size()));
			return map;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return null;
	}
	
	/**
	 * 双向关联哈希表，互为键值对
	 * @param key1	表1
	 * @param key2	表2
	 * @param field1	值1
	 * @param field2	值2
	 * @return	修改条目数|失败返回-1
	 */
	public static long dhset(String key1, String key2, String field1, String field2) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long l = 0;
			l += jedis.hset(key1, field1, field2);
			l += jedis.hset(key2, field2, field1);
			logger.info("dhset: " + key1 + " - " + key2 + " - " + field1 + " - " + field2);
			return l;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
	
	/**
	 * 删除双向哈希表中的一对值
	 * @param key1	表1
	 * @param key2	表2
	 * @param field1	键1
	 * @param field2	键2
	 * @return	修改条目数|失败返回-1
	 */
	public static long dhdel(String key1, String key2, String field1, String field2) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			long l = 0;
			if(field1 != null) {
				String value1 = jedis.hget(key1, field1);
				l += jedis.hdel(key1, field1);
				if(value1 != null)
					l += jedis.hdel(key2, value1);
			}
			if(field2 != null) {
				String value2 = jedis.hget(key2, field2);
				l += jedis.hdel(key2, field2);
				if(value2 != null)
					l += jedis.hdel(key1, value2);
			}
			logger.info("dhdel: " + key1 + " - " + key2 + " - " + field1 + " - " + field2);
			return l;
		}
		catch (Exception e) {
			logger.error(e);
		}
		finally {
			if(jedis != null)
				jedis.close();
		}
		return -1;
	}
}