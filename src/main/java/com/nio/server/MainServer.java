package com.nio.server;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.eShare.utils.Utils;
import com.nio.constant.RedisKey;
import com.nio.handler.MsgDecoder;
import com.nio.handler.MsgEncoder;
import com.nio.handler.SeverHandler;
import com.nio.process.MessageMonitor;
import com.nio.utils.JedisUtil;
import com.nio.utils.PropertyUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class MainServer {
	public void bind(int port) throws Exception {

		// 服务器线程组 用于网络事件的处理 一个用于服务器接收客户端的连接
		// 另一个线程组用于处理SocketChannel的网络读写
		EventLoopGroup bossGroup = new NioEventLoopGroup(2);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		// 添加注释
		try {
			// NIO服务器端的辅助启动类 降低服务器开发难度
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			/**
			 * 使用内存池之后，内存的申请和释放必须成对出现，即retain()和release()要成对出现，
			 * 否则会导致内存泄露。
			 * 值得注意的是，如果使用内存池，完成ByteBuf的解码工作之后必须显式的调用ReferenceCountUtil.
			 * release(msg)对接收缓冲区ByteBuf进行内存释放， 否则它会被认为仍然在使用中，这样会导致内存泄露。
			 */
			
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)// 类似NIO中serverSocketChannel
					.option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
					.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)// 动态扩容
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// 使用内存池，
					.childHandler(new ChildChannelHandler());// 最后绑定I/O事件的处理类
																// 处理网络IO事件

			// 服务器启动后 绑定监听端口 同步等待成功 主要用于异步操作的通知回调 回调处理用的ChildChannelHandler
			ChannelFuture f = serverBootstrap.bind(port).sync();
			Utils.formatPrint("netty服务器启动......");
			
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出 释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			Utils.formatPrint("服务器优雅的释放了线程资源...");
		}

	}

	/**
	 * 网络事件处理器
	 */
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();

			pipeline.addLast("idleStateHandler", new IdleStateHandler(0,PropertyUtils.WRITER_IDLE_TIME_SECONDS,0));
			pipeline.addLast("decoder", new MsgDecoder());
			pipeline.addLast("encoder", new MsgEncoder());
			
			pipeline.addLast("service", new SeverHandler());
		}

	}

	public static void main(String[] args) throws Exception {
		
		//清除数据
		clear();
		
		//监控消息队列的消息
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new MessageMonitor(), 1000L,100L, TimeUnit.MILLISECONDS);
		
		new MainServer().bind(PropertyUtils.PORT);
		
		
		
	}
	
	public static void clear(){
		JedisUtil.del(RedisKey.ONLINE_USERS);
		JedisUtil.del(RedisKey.USER_TOKEN);
	}
}
