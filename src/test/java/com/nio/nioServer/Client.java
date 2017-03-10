package com.nio.nioServer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.eShare.sys.SysThreadPool;
import com.eShare.utils.Utils;
import com.nio.constant.RedisKey;
import com.nio.utils.JedisUtil;

import redis.clients.jedis.Jedis;

public class Client {

	static long preTime = System.currentTimeMillis();
	
	static long now = 0 ;
	
	static int index = 0 ;
	
	public static void main(String[] args) {
		
		
		
		SysThreadPool.getInstance().scheduleAtFixedRate(new Runnable(){
			
			public void run() {
				
					Jedis jedis = JedisUtil.getResource();
					
					Utils.formatPrint(Thread.currentThread().getName());
					
					try{
						List<String> list = jedis.brpop(2,RedisKey.NOTIFY_MESSAGE);
						
						Utils.formatPrint(JSON.toJSONString(list));
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						jedis.close();
					}
					
					
				
			}
			
		}, 1000L, 100L, TimeUnit.MILLISECONDS);

	}

}
