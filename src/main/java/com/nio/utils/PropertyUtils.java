package com.nio.utils;

import java.util.Properties;

/**
 * 
 * @author martin
 *
 * 时间  2016年5月14日 下午4:59:31
 *
 * 说明  读取系统文件配置
 */
public class PropertyUtils {
	
	/** redis配置参数  */
	public static String REDIS_HOST;
	public static int REDIS_PORT;
	public static String REDIS_AUTH;
	
	/** socket配置参数  */
	public static int PORT;
	public static int MAX_UN_REC_PING_TIMES;
	
	/** 心跳频率 */
	public static int WRITER_IDLE_TIME_SECONDS ;
	
	/** 当前版本号   */
	public static short VERSION ;
	
	static {
		Properties prop = new Properties();
		try {
			prop.load(PropertyUtils.class.getClassLoader().getResourceAsStream("config/system.properties"));
			
			REDIS_HOST = prop.getProperty("redis.host");
			REDIS_PORT = Integer.parseInt(prop.getProperty("redis.port"));
			REDIS_AUTH = prop.getProperty("redis.auth");
			
			PORT = Integer.parseInt(prop.getProperty("port"));
			MAX_UN_REC_PING_TIMES = Integer.parseInt(prop.getProperty("max_un_rec_ping_times"));
			WRITER_IDLE_TIME_SECONDS = Integer.parseInt(prop.getProperty("writer_idle_time_seconds"));
			
			VERSION = Short.parseShort(prop.getProperty("version"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
