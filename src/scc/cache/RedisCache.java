package scc.cache;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scc.data.*;
import scc.utils.Session;
import jakarta.ws.rs.core.NewCookie;



public class RedisCache {
	//private static final String RedisHostname = System.getenv("REDIS_URL");
	//private static final String RedisKey = System.getenv("REDIS_KEY");
	private static final String RedisHostname = System.getenv("REDIS");

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
		instance = new JedisPool(poolConfig, RedisHostname, 6379);
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

	public synchronized static UserDAO getUserFromCache (String id) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			return mapper.readValue(jedis.get("user:" + id), UserDAO.class);
		} catch (Exception e) {
			throw new NotFoundException();
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

	public synchronized static void removeAuctionFromCache (String id) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.del("auction:" + id);
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static AuctionDAO getAuctionFromCache (String id) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			return mapper.readValue(jedis.get("auction:" + id), AuctionDAO.class);
		} catch (Exception e) {
			throw new NotFoundException();
		}
	}

	public synchronized static void putSession(NewCookie c, Session s){
		ObjectMapper mapper = new ObjectMapper();
		try(Jedis jedis = RedisCache.getCachePool().getResource()){
			jedis.set(c.getValue(), mapper.writeValueAsString(s));
		}catch(Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static Session getSession (String s) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			return mapper.readValue(jedis.get(s), Session.class);
		} catch (Exception e) {
			throw new NotFoundException();
		}
	}

	public synchronized static void addBidToCache (BidDAO bid) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.set("bid:"+ bid.getId(), mapper.writeValueAsString(bid));
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static BidDAO getBidFromCache (String id) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			return mapper.readValue(jedis.get("bid:" + id), BidDAO.class);
		} catch (Exception e) {
			throw new NotFoundException();
		}
	}

	public synchronized static void removeBidFromCache (String id) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.del("bid:" + id);
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static void addQuestionToCache (QuestionDAO q) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.set("question:"+ q.getId(), mapper.writeValueAsString(q));
		} catch (Exception e) {
			throw new BadRequestException();
		}
	}

	public synchronized static QuestionDAO getQuestionFromCache (String id) {
		ObjectMapper mapper = new ObjectMapper();
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			return mapper.readValue(jedis.get("question:" + id), QuestionDAO.class);
		} catch (Exception e) {
			throw new NotFoundException();
		}
	}
 
}
