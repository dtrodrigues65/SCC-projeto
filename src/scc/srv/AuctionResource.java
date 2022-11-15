package scc.srv;

import scc.data.*;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.azure.cosmos.implementation.InternalServerErrorException;
import com.azure.cosmos.models.CosmosItemResponse;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
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
			
			UserDAO u =  CosmosDBLayer.getInstance().getUserById(auction.getUser());
			Set<String> userAuctions = u.getAuctionsIds();
			userAuctions.add(uid);
			u.setAuctionsIds(userAuctions);
			UsersResource.updateUser(session, u);
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

	@POST
	@Path("/{auctionId}/bid")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BidDAO createBid( @CookieParam("scc:session") Cookie session, BidDAO bid){
		try  {
			UsersResource.checkCookieUser(session, bid.getUser());
			String uid = UUID.randomUUID().toString();
        	bid.setId(uid);
			AuctionDAO a =  CosmosDBLayer.getInstance().getAuctionById(bid.getAuctionId());
			Set<String> auctionsBid = a.getBidIds();
			UserDAO u = CosmosDBLayer.getInstance().getUserById(bid.getUser());
			Set<String> userBids = u.getBidsIds();
			CosmosItemResponse<BidDAO> res = null;
			if(Float.valueOf(bid.getValue()) > Float.valueOf(a.getMinPrice()) && a.getStatus().equals("OPEN")){
				auctionsBid.add(bid.getId());
				a.setBidIds(auctionsBid);
				a.setMinPrice(bid.getValue());
				userBids.add(uid);
				u.setBidsIds(userBids);
				UsersResource.updateUser(session, u);
				res = resBid (CosmosDBLayer.getInstance().putBid(bid));
				resAuction (CosmosDBLayer.getInstance().updateAuction(a));

			}
			if(res == null) {
				throw new Exception("Bid is less than min price");
			}
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}
	}

	@GET
	@Path("/{auctionId}/bid")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<BidDAO> listBids(@PathParam("auctionId") String id) {
		Set<BidDAO> aux = new HashSet<BidDAO>();
		AuctionDAO d  = CosmosDBLayer.getInstance().getAuctionById(id);
		Set<String> s  = d.getBidIds();
		for (String x: s ) {
			BidDAO b = CosmosDBLayer.getInstance().getBidById(x);
			aux.add(b);
		}
		return aux;
	}

	private CosmosItemResponse<AuctionDAO> resAuction (CosmosItemResponse<AuctionDAO> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}

	private CosmosItemResponse<BidDAO> resBid (CosmosItemResponse<BidDAO> res) {
		if (res.getStatusCode() < 300) {
			return res;
		} else {
			throw new NotFoundException();
		} 
	}

}
