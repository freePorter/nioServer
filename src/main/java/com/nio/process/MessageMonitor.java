package com.nio.process;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import com.eShare.sys.SysThreadPool;
import com.eShare.utils.Utils;
import com.nio.constant.ChatType;
import com.nio.constant.MsgType;
import com.nio.constant.RedisKey;
import com.nio.constant.SysConstant;
import com.nio.entity.Message;
import com.nio.entity.MessageHeader;
import com.nio.server.ChannelManager;
import com.nio.utils.JedisUtil;
import com.nio.utils.PropertyUtils;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import redis.clients.jedis.Jedis;

/**
 * 
 * @author martin
 *
 * 时间 2016年5月15日 下午4:00:54
 *
 * 说明 监控消息队列
 */
public class MessageMonitor implements Runnable {

	public void run() {
		
		    Map<String,Channel> channelMap = ChannelManager.getChannelMap();

		    if (channelMap.size() == 0) {
		      return;
		    }

		    Jedis jedis = JedisUtil.getResource();

		    List<String> list = null ;
		    
		    try{
		    	list = jedis.brpop(2, RedisKey.NOTIFY_MESSAGE);
		    	
		    	if(list == null)
		    		return ;

		    }catch(Exception e){
		    	e.printStackTrace();
		    	Utils.formatPrint(e.getMessage());
		    }finally{
				if (jedis != null) {
					jedis.close();
				}
		    }
		    
		    String msgId = list.get(1);
					
			Utils.formatPrint(Thread.currentThread().getName());
			
			if(msgId.equals(RedisKey.NOTIFY_MESSAGE))
				return ;
			
			Jedis jedis2 = JedisUtil.getResource();
			
			String msgContent = jedis2.hget(RedisKey.MESSAGE_DETAIL, msgId);
			
			if (jedis2 != null) {
				jedis2.close();
			}
			
			
			byte[] content = null;
		
			try {
				content = new String(msgContent.getBytes(), SysConstant.DEFAULT_ENCODE).getBytes();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				Utils.formatPrint(e.getMessage());
			}
		
			// 消息体长度
			final int length = content.length;
		
			MessageHeader msgHeader = new MessageHeader(PropertyUtils.VERSION, 
					                                    ChatType.BROADCAST, 
					                                    MsgType.TEXT,
					                                    SysConstant.ADMIN_WAITER, 
					                                    SysConstant.ALL_USERS, 
					                                    length);
		
			Message msg = new Message();
			msg.setHeader(msgHeader);
			msg.setContent(content);
			
			// 循环向在线用户发送消息
			for (String key : channelMap.keySet()) {
				
				SysThreadPool.getInstance().execute(new Runnable(){
					
					public void run() {
						
						Channel ch = channelMap.get(key);
						
						//删除未激活的通道
						if(!ch.isActive()){
							ChannelManager.removeChannel(ch);
						}else{
							ch.writeAndFlush(msg);
						}
					}
				});
				
				
			}
			
			ReferenceCountUtil.release(msg);
	}

}
