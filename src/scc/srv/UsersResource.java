package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import scc.cache.RedisCache;
import scc.data.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;

import com.azure.cosmos.models.CosmosItemResponse;
import scc.utils.Login;
import scc.utils.Session;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


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
	public UserDAO delUser(@CookieParam("scc:session") Cookie session, UserDAO user) {
		try {
			checkCookieUser(session, user.getId());
			auxDel(user.getId());
			resObject(CosmosDBLayer.getInstance().delUser(user));
			return user;
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}
		
	}

	private synchronized void auxDel(String u){
		UserDAO user = CosmosDBLayer.getInstance().getUserById(u);
		Set<String> auctions = user.getAuctionsIds();
		Set<String> bids = user.getBidsIds();
		for(String sa: auctions){
			AuctionDAO a =  CosmosDBLayer.getInstance().getAuctionById(sa);
			a.setUser("Deleted user");
			CosmosDBLayer.getInstance().updateAuction(a);
		}
		for(String sb: bids){
			BidDAO b =  CosmosDBLayer.getInstance().getBidById(sb);
			b.setUser("Deleted user");
			CosmosDBLayer.getInstance().updateBid(b);
		}
	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static UserDAO updateUser(@CookieParam("scc:session") Cookie session, UserDAO user) {
		try {
			checkCookieUser(session, user.getId());
			CosmosItemResponse<UserDAO> res = resUser(CosmosDBLayer.getInstance().updateUser(user));
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}

	}

	@GET
	@Path("/{id}/listAuctions")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<AuctionDAO> listAuctions(@PathParam("id") String id, @QueryParam("status") String status) {
		UserDAO u = CosmosDBLayer.getInstance().getUserById(id);
		Set<String> setAuctions = u.getAuctionsIds();
		Set <AuctionDAO> aux = new HashSet<AuctionDAO>();
		if (status != null ){
			if(status.equals("OPEN")) {
				for (String s : setAuctions)  {
					AuctionDAO a = CosmosDBLayer.getInstance().getAuctionById(s);
					if (a.getStatus().equals("OPEN")) {
						aux.add(a);
					}
				}
			}
		} else {
			for (String s : setAuctions)  {
				AuctionDAO a = CosmosDBLayer.getInstance().getAuctionById(s);
				aux.add(a);	
			}
		}
		return aux;
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
			RedisCache.putSession(cookie, new Session(uid, user.getUser()));
			return Response.ok().cookie(cookie).build();
		}else{
			throw new NotAuthorizedException("Incorrect login");
		}
	}

	
	private CosmosItemResponse<Object> resObject (CosmosItemResponse<Object> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}

	private static CosmosItemResponse<UserDAO> resUser (CosmosItemResponse<UserDAO> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}

	public static Session checkCookieUser(Cookie session, String id) throws NotAuthorizedException {
		if (session == null || session.getValue() == null)
			throw new NotAuthorizedException("No session initialized");
		Session s;
		try {
			s = RedisCache.getSession(session.getValue());
		} catch (Exception e) {
			throw new NotAuthorizedException("No valid session initialized");
		}
		if (s == null || s.getUser() == null || s.getUser().length() == 0)
			throw new NotAuthorizedException("No valid session initialized");
		if (!s.getUser().equals(id))
			throw new NotAuthorizedException("Invalid user : " + s.getUser());
		return s;
	}

}

