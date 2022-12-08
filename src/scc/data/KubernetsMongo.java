package scc.data;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

import scc.cache.RedisCache;

public class KubernetsMongo {
    
    private static final boolean CACHE_FLAG = true;

    private static KubernetsMongo instance;

    public static synchronized KubernetsMongo getInstance() {
		if( instance != null)
			return instance;

        MongoClient client = MongoClients.create();

        instance = new KubernetsMongo(client);
		return instance;
	}

    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection users;

    public KubernetsMongo(MongoClient client) {
		this.client = client;
        db.createCollection("users");

	}

    private synchronized void init() {
		if( db != null)
			return;
		db = client.getDatabase("mydb");
		users = db.getCollection("users");
	}

    public DeleteResult delUser(UserDAO user) {
		init();
		if (CACHE_FLAG) {
			try{
				RedisCache.removeUserFromCache(user.getId());
			}catch(Exception e){
			}
		}
        Bson query = eq(user);
		return users.deleteOne(query);
	}

    public InsertOneResult putUser(UserDAO user) throws JsonProcessingException {
		init();
		if (CACHE_FLAG) {
			try{
				RedisCache.addUserToCache(user);
			}catch(Exception e){
			}
		}
        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(user);
        Document d = Document.parse(jsonString);
		return users.insertOne(d);
	}

    public UserDAO getUserById(String id) {
        init();
        UserDAO user = null;
        if (CACHE_FLAG) {
			try{
				user = RedisCache.getUserFromCache(id);
			}catch(Exception e){
                Bson query = eq("id",id);
				FindIterable<UserDAO> f = users.find (query);
                user = f.iterator().next();
			}
		} else {
            Bson query = eq("id",id);
			FindIterable<UserDAO> f = users.find (query);
            user = f.iterator().next();
        }
        return user;
    }






}
