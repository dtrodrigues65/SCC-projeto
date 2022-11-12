package scc.srv;

import scc.data.*;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;

import java.util.UUID;

import com.azure.cosmos.implementation.InternalServerErrorException;
import com.azure.cosmos.models.CosmosItemResponse;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;

import scc.srv.UsersResource;

@Path("/auction")
public class AuctionResource {

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuctionDAO putAuction(@CookieParam("scc:session") Cookie session, AuctionDAO auction) {
		try  {
			UsersResource.checkCookieUser(session, auction.getUser());
			String uid = UUID.randomUUID().toString();
        	auction.setId(uid);
        	CosmosItemResponse<AuctionDAO> res = resAuction (CosmosDBLayer.getInstance().putAuction(auction));
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}
    }

    @PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AuctionDAO updateAuction( @CookieParam("scc:session") Cookie session, AuctionDAO auction) {
		try  {
			UsersResource.checkCookieUser(session, auction.getUser());
			CosmosItemResponse<AuctionDAO> res = resAuction (CosmosDBLayer.getInstance().updateAuction(auction));
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}
	}

	private CosmosItemResponse<AuctionDAO> resAuction (CosmosItemResponse<AuctionDAO> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}
}
