package Main;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

public class RedisImpl {
	/*public static final String REDIS_HOST="23.23.156.130";
	public static final int REDIS_PORT=16379;
	public static final String REDIS_PASSWORD="p7mpbpvga2h96v60jdom3g07164";
	*/
	public static final String VISITOR_SET="VISITOR_SET";
	public static final String VISITOR_ID_SET="VISITOR_ID_SET";
	public static final String CONTENT_SET="CONTENT_SET";
	public static final String CONTENT_ID_SET="CONTENT_ID_SET";
	
	
	public void RedisStartup() {
		/*JedisShardInfo shardInfo = new JedisShardInfo(REDIS_HOST, REDIS_PORT);
		shardInfo.setPassword(REDIS_PASSWORD);*/
		//final Jedis subscriberJedis = new Jedis(shardInfo);
		final Jedis subscriberJedis = new Jedis("localhost");
		
		
	}
	

}
