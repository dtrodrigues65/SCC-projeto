package scc.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import scc.datafunction.*;

import jakarta.ws.rs.BadRequestException;

public class RedisCache {

	private static final String RedisHostname = System.getenv("REDIS_URL");
	private static final String RedisKey = System.getenv("REDIS_KEY");
	
	private static JedisPool instance;
	
	public synchronized static JedisPool getCachePool() {
		if( instance != null)
			return instance;
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		instance = new JedisPool(poolConfig, RedisHostname, 6380, 1000, RedisKey, true);
		return instance;
		
	}

	public synchronized static void removeAuctionFromCache (String id) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.del("auction:" + id);
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static void addAuctionToCache (AuctionDAO auction) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.set("auction:"+ auction.getId(), mapper.writeValueAsString(auction));
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}


}
