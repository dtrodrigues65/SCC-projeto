package scc.cosmos;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

import jakarta.ws.rs.NotFoundException;
import scc.cache.RedisCache;

import scc.datafunction.*;

public class CosmosDBLayer {
	private static final String CONNECTION_URL = System.getenv("COSMOS_CONNECTION_URL");
	private static final String DB_KEY = System.getenv("COSMOS_DB_KEY");
	private static final String DB_NAME = System.getenv("COSMOS_DB_NAME");
	
	private static CosmosDBLayer instance;

	public static synchronized CosmosDBLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = new CosmosClientBuilder()
		         .endpoint(CONNECTION_URL)
		         .key(DB_KEY)
		         .gatewayMode()		
		         .consistencyLevel(ConsistencyLevel.SESSION)
		         .connectionSharingAcrossClientsEnabled(true)
		         .contentResponseOnWriteEnabled(true)
		         .buildClient();
		instance = new CosmosDBLayer( client);
		return instance;
		
	}
	
	private CosmosClient client;
	private CosmosDatabase db;
	private CosmosContainer auctions;
	
	public CosmosDBLayer(CosmosClient client) {
		this.client = client;
	}
	
	private synchronized void init() {
		if( db != null)
			return;
		db = client.getDatabase(DB_NAME);
		auctions = db.getContainer("auctions");
	}

    public Set<AuctionDAO> getAllAuctions () {
		init();
		Set<AuctionDAO> setAuctions = new HashSet<AuctionDAO>();
		CosmosPagedIterable<AuctionDAO> b = auctions.queryItems("SELECT * FROM auctions", new CosmosQueryRequestOptions(), AuctionDAO.class);
		Iterator<AuctionDAO> it = b.iterator();
		try{
			while(it.hasNext()) {
				AuctionDAO a = it.next();
				setAuctions.add(a);
			}
		}catch(Exception e2){
			throw new NotFoundException();
		}
		return setAuctions; 
	}

    public CosmosItemResponse<AuctionDAO> updateAuction(AuctionDAO auction) {
		init();
		try{
			RedisCache.removeAuctionFromCache(auction.getId());
			RedisCache.addAuctionToCache(auction);
		}catch(Exception e){
		}
		PartitionKey key = new PartitionKey(auction.getId());
        return auctions.replaceItem(auction, auction.getId(), key, new CosmosItemRequestOptions());
	}

}
