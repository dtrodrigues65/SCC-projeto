package scc.srv;

import scc.data.*;


import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;

import com.azure.cosmos.models.CosmosItemResponse;


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

}

