package com.nio.nioServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.eShare.sys.SysThreadPool;
import com.eShare.utils.ConvertUtil;
import com.eShare.utils.Utils;
import com.nio.constant.RedisKey;
import com.nio.process.MessageMonitor;
import com.nio.utils.JedisUtil;

import redis.clients.jedis.Jedis;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	
	public void test6() {

		Jedis jedis = JedisUtil.getResource();
		
		List<String> list = jedis.blpop(1000, RedisKey.NOTIFY_MESSAGE);
		
		for (String str:list) {
			System.out.println("str---------->"+str);
		}

		jedis.close();

	}

	@Test
	public void test5() {

		Jedis jedis = JedisUtil.getResource();
		
		String message = "“文化大革命”是我们党和国家发展进程中的一个重大曲折。应该如何认识“文革”？1980年8月，邓小平同志两次会见意大利记者法拉奇，以坦荡的历史胸襟和客观鲜明的政治态度回答了当时国内国际都非常关注的中国共产党对毛泽东同志和“文化大革命”的评价问题。一年后，党的十一届六中全会通过了《关于建国以来党的若干历史问题的决议》，对新中国成立以来的一系列重大历史问题作出正确结论，彻底否定了“文化大革命”和“无产阶级专政下继续革命的理论”，实事求是地评价了毛泽东同志的历史地位，充分论述了毛泽东思想作为党的指导思想的伟大意义。这个决议对“文革”的政治定性和原因分析，经受住了实践的检验、人民的检验和历史的检验，具有不可动摇的科学性和权威性。";
				

		for (int i = 0; i < 1000; i++) {
			jedis.rpush(RedisKey.NOTIFY_MESSAGE,message);
		}

		jedis.close();

	}

	public void test4() {
		Utils.formatPrint("");
		Jedis jedis = JedisUtil.getResource();

		List<String> list = jedis.blpop(0, RedisKey.NOTIFY_MESSAGE);

		for (String str : list) {
			System.out.println("----->" + str);
		}

		jedis.close();
	}

	public void test3() {

		ScheduledExecutorService schedule = Executors.newScheduledThreadPool(5);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println(" begin to do something at:" + sdf.format(new Date()));
		schedule.scheduleWithFixedDelay(new MessageMonitor(), 1, 2, TimeUnit.SECONDS);

	}

	public void test2() {

		long time = 1000L;

		int fromId = 8888;

		byte[] timeArr = ConvertUtil.intToBytes(fromId);

		System.out.println(timeArr.length);

		timeArr = ConvertUtil.longToBytes(time);

		System.out.println(timeArr.length);

	}

	public void test() throws Exception {

		String str = new String("我是中国人abc123".getBytes(), "UTF-8");

		byte[] bytes = str.getBytes();

		System.out.println("我是中国人abc123".getBytes().length);

		System.out.println(bytes.length);

		String content = "NeaciDHml6XvvIzkuK3lhbHmtbflj6PluILlp5TkvZzlh7rpl67otKPlhrPlrprvvJrkuK3lhbHnp4Doi7HljLrlp5Tlia/kuaborrDjgIHljLrkurrmsJHmlL/lupzljLrplb/jgIHnkLzljY7mnZHmi4bov53ooYzliqjmgLvmjIfmjKXpu4TpuL/lhJLvvIznlo/kuo7nrqHnkIbnm5HnnaPvvIzlr7nmi4bov53ooYzliqjnu4Tnu4fpooblr7zkuI3lipvvvIzlr7nooYzliqjpo47pmanor4TliKTkuI3lpJ/vvIzlr7nlubLpg6jmlZnogrLnrqHnkIbkuI3liLDkvY3vvIzlr7zoh7Tlj5HnlJ/ph43lpKfkuovku7bvvIzotJ/mnInph43opoHpooblr7zotKPku7vjgILmoLnmja7jgIrlhbPkuo7lrp7ooYzlhZrmlL/pooblr7zlubLpg6jpl67otKPnmoTmmoLooYzop4TlrprjgIvnrKzkupTmnaHnrKzkuInmrL7jgIHnrKzkuIPmnaHlkozjgIrmtbfljZfnnIHigJzlurjmh5LmlaPlpaLotKrigJ3ooYzkuLrpl67otKPlip7ms5XvvIjor5XooYzvvInjgIvnrKzkupTmnaHnrKzlm5vmrL7jgIHnrKzlha3mnaHop4TlrprvvIzluILlp5TlkIzmhI/pu4TpuL/lhJLlvJXlko7ovp7ljrvnp4Doi7HljLrljLrplb/ogYzliqHvvIzlkIzml7blhY3ljrvlhbbnp4Doi7HljLrlp5Tlia/kuaborrDjgIHluLjlp5TjgIHlp5TlkZjogYzliqHjgII=";

		System.out.println(content.getBytes().length);
	}
}
