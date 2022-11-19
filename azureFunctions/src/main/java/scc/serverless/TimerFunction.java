package scc.serverless;

import java.text.SimpleDateFormat;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.datafunction.AuctionDAO;
import scc.cosmos.CosmosDBLayer;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.microsoft.azure.functions.*;

/**
 * Azure Function with Timer Trigger to cache flush
 */
public class TimerFunction {
    @FunctionName("cache-flush")
    public void cosmosFunction( @TimerTrigger(name = "cacheflush", 
    								schedule = "0 */60 * * * *") 
    				String timerInfo,
    				ExecutionContext context) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.flushAll();
		} catch (Exception e) {
		}
    }

	/**
 * Azure Function with Timer Trigger to close auctions
 	*/
	@FunctionName("close-auction")
	public void closeAuctionFunction( @TimerTrigger(name= "closeauction",
										schedule = "0 */1 * * * *")
					String timerInfo,
					ExecutionContext context) {
		
		Set<AuctionDAO> allAuctions = CosmosDBLayer.getInstance().getAllAuctions();
		Date currentTime = getDateTime(java.time.LocalDateTime.now().toString().split("[.]") [0]);
		for(AuctionDAO a: allAuctions){
			Date auctionTime = getDateTime(a.getEndTime().split("[.]") [0]);
			if(auctionTime.before(currentTime) && a.getStatus().equals("OPEN")) {
				a.setStatus("CLOSE");
				a.setWinnerBid(a.getLastBid());
				CosmosDBLayer.getInstance().updateAuction(a);
			}
		}
	}

	private Date getDateTime(String d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date time = null;
		try {
			time = sdf.parse(d);
		} catch (ParseException e) {
		}
		return time;
	}
}
