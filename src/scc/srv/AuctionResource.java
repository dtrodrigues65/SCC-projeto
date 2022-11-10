package scc.srv;

import scc.data.*;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;

@Path("/auction")
public class AuctionResource {

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuctionDAO putAuction(AuctionDAO auction) {
        return CosmosDBLayer.getInstance().putAuction(auction).getItem();
    }
}
