package scc.srv;

import scc.data.*;


import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
		return CosmosDBLayer.getInstance().putUser(user).getItem();
	}


	//mudar depois o que for preciso
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDAO delUserById(@PathParam("id") String id) {
		//UserDAO user = CosmosDBLayer.getInstance().getUserById(id).iterator().next();
		return (UserDAO) CosmosDBLayer.getInstance().delUserById(id).getItem();
	}

}
