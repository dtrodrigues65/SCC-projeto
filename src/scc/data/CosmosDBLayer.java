package scc.data;

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

public class CosmosDBLayer {
	//private static final String CONNECTION_URL = "https://scctp1cosmosdb.documents.azure.com:443/";
	private static final String CONNECTION_URL = System.getenv("COSMOS_CONNECTION_URL");
	//private static final String DB_KEY = "aRQeHIFXAlwba2rPs34mPitOB98ALBOyPnlbAmZbPjDyT6d4KIRgKqJHOhABaXixhqdRUybgue37JR4ve95GLw==";
	private static final String DB_KEY = System.getenv("COSMOS_DB_KEY");
	//private static final String DB_NAME = "scctp1db";
	private static final String DB_NAME = System.getenv("COSMOS_DB_NAME");
	
	private static CosmosDBLayer instance;

	public static synchronized CosmosDBLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = new CosmosClientBuilder()
		         .endpoint(CONNECTION_URL)
		         .key(DB_KEY)
		         //.directMode()
		         .gatewayMode()		
		         // replace by .directMode() for better performance
		         .consistencyLevel(ConsistencyLevel.SESSION)
		         .connectionSharingAcrossClientsEnabled(true)
		         .contentResponseOnWriteEnabled(true)
		         .buildClient();
		instance = new CosmosDBLayer( client);
		return instance;
		
	}
	
	private CosmosClient client;
	private CosmosDatabase db;
	private CosmosContainer users;
	private CosmosContainer auctions;
	private CosmosContainer bids;
	private CosmosContainer questions;
	
	public CosmosDBLayer(CosmosClient client) {
		this.client = client;
	}
	
	private synchronized void init() {
		if( db != null)
			return;
		db = client.getDatabase(DB_NAME);
		auctions = db.getContainer("auctions");
		users = db.getContainer("users");
		bids = db.getContainer("bids");
		questions = db.getContainer("questions");
	}

	/* 
	public CosmosItemResponse<Object> delUserById(String id) {
		init();
		PartitionKey key = new PartitionKey( id);
		return users.deleteItem(id, key, new CosmosItemRequestOptions());
	}
	*/

	public CosmosItemResponse<Object> delUser(UserDAO user) {
		init();
		try{
            RedisCache.removeUserFromCache(user.getId());
        }catch(Exception e){
        }
		return users.deleteItem(user, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
		init();
		try{
            RedisCache.addUserToCache(user);
        }catch(Exception e){
        }
		return users.createItem(user);
	}

	public UserDAO getUserById(String id) {
		init();
		UserDAO user = null;
		try{
			user = RedisCache.getUserFromCache(id);
		}catch(Exception e){
			CosmosPagedIterable<UserDAO> u = users.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
			try{
				user = u.iterator().next();
			}catch(Exception e2){
				throw new NotFoundException();
			}
			RedisCache.addUserToCache(user);
		}
		return user;
	}
	/*
	public CosmosPagedIterable<UserDAO> getUsers() {
		init();
		return users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
	}
	*/

	public CosmosItemResponse<UserDAO> updateUser(UserDAO user) {
		init();
		try{
			RedisCache.removeUserFromCache(user.getId());
            RedisCache.addUserToCache(user);
        }catch(Exception e){
        }
		PartitionKey key = new PartitionKey(user.getId());
		return users.replaceItem(user, user.getId(), key, new CosmosItemRequestOptions());
	}


	
	public CosmosItemResponse<AuctionDAO> putAuction(AuctionDAO auction) {
		init();
		try{
			RedisCache.addAuctionToCache(auction);
		}catch(Exception e){
		}
		return auctions.createItem(auction);
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

	public void close() {
		client.close();
	}

	public AuctionDAO getAuctionById(String id) {
		init();
		AuctionDAO auction = null;
		try{
			auction = RedisCache.getAuctionFromCache(id);
		}catch(Exception e){
			CosmosPagedIterable<AuctionDAO> a = auctions.queryItems("SELECT * FROM auctions WHERE auctions.id=\"" + id + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
			try{
				auction = a.iterator().next();
			}catch(Exception e2){
				throw new NotFoundException();
			}
			RedisCache.addAuctionToCache(auction);
		}
		return auction;
	}
	
	public CosmosItemResponse<BidDAO> putBid(BidDAO bid) {
		init();
		try{
			RedisCache.addBidToCache(bid);
		}catch(Exception e){
		}
		return bids.createItem(bid);
	}

	public BidDAO getBidById(String id) {
		init();
		BidDAO bid = null;
		try{
			bid = RedisCache.getBidFromCache(id);
		}catch(Exception e){
			CosmosPagedIterable<BidDAO> b = bids.queryItems("SELECT * FROM bids WHERE bids.id=\"" + id + "\"", new CosmosQueryRequestOptions(), BidDAO.class);
			try{
				bid = b.iterator().next();
			}catch(Exception e2){
				throw new NotFoundException();
			}
			RedisCache.addBidToCache(bid);
		}
		return bid;
	}

	public CosmosItemResponse<BidDAO> updateBid(BidDAO bid) {
		init();
		try{
			RedisCache.removeBidFromCache(bid.getId());
			RedisCache.addBidToCache(bid);
		}catch(Exception e){
		}
		PartitionKey key = new PartitionKey(bid.getId());
        return bids.replaceItem(bid, bid.getId(), key, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<QuestionDAO> putQuestion(QuestionDAO q) {
		init();
		try{
			RedisCache.addQuestionToCache(q);
		}catch(Exception e){
		}
		return questions.createItem(q);
	}

	public QuestionDAO getQuestionById (String id) {
		init();
		QuestionDAO question = null;
		try{
			question = RedisCache.getQuestionFromCache(id);
		}catch(Exception e){
			CosmosPagedIterable<QuestionDAO> b = questions.queryItems("SELECT * FROM questions WHERE questions.id=\"" + id + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class);
			try{
				question = b.iterator().next();
			}catch(Exception e2){
				throw new NotFoundException();
			}
			RedisCache.addQuestionToCache(question);
		}
		return question;
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
	
}
