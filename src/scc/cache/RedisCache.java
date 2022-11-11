package scc.cache;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.BadRequestException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scc.data.UserDAO;

public class RedisCache {
	private static final String RedisHostname = "scctp1cache.redis.cache.windows.net";
	private static final String RedisKey = "vgvsJ2tONf2NdY8XinpUUyn4loKKV11VjAzCaEFqx50=";
	
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

	public synchronized static void addUserToCache (UserDAO user) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.set("user:"+ user.getId(), mapper.writeValueAsString(user));
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static void removeUserFromCache (String id) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.del("user:" + id);
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}
}
