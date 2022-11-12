package scc.srv;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.data.*;


import jakarta.ws.rs.core.MediaType;

import com.azure.cosmos.models.CosmosItemResponse;
import scc.utils.Login;
import scc.utils.Session;

import java.util.UUID;

import static scc.cache.RedisCache.putSession;


@Path("/user")
public class UsersResource{

    @POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserDAO putUser(UserDAO user) {
		CosmosItemResponse<UserDAO> res = resUser(CosmosDBLayer.getInstance().putUser(user));
		return res.getItem(); 
	}

	@DELETE
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserDAO delUser(UserDAO user) {
		resObject(CosmosDBLayer.getInstance().delUser(user));
		return user;
	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserDAO updateUser(UserDAO user) {
		CosmosItemResponse<UserDAO> res = resUser(CosmosDBLayer.getInstance().updateUser(user));
		return res.getItem();
	}



	private CosmosItemResponse<Object> resObject (CosmosItemResponse<Object> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}

	private CosmosItemResponse<UserDAO> resUser (CosmosItemResponse<UserDAO> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}

	@POST
	@Path("/auth")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response auth(Login user){
		boolean pwdOk = false;
		UserDAO u = CosmosDBLayer.getInstance().getUserById(user.getUser());
		if(u.getPwd().equals(user.getPwd())){
			pwdOk = true;
		}
		if(pwdOk){
			String uid = UUID.randomUUID().toString();
			NewCookie cookie = new NewCookie.Builder("scc:session")
					.value(uid)
					.path("/")
					.comment("sessionid")
					.maxAge(3600)
					.secure(false)
					.httpOnly(true)
					.build();
			putSession(cookie, new Session(uid, user.getUser()));
			return Response.ok().cookie(cookie).build();
		}else{
			throw new NotAuthorizedException("Incorrect login");
		}
	}

}

