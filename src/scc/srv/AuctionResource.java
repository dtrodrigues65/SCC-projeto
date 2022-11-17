package scc.srv;

import scc.data.*;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	public BidDAO createBid( @CookieParam("scc:session") Cookie session, @PathParam("auctionId") String id, BidDAO bid){
		try  {
			UsersResource.checkCookieUser(session, bid.getUser());
			String uid = UUID.randomUUID().toString();
        	bid.setId(uid);
			AuctionDAO a =  CosmosDBLayer.getInstance().getAuctionById(id);
			Set<String> auctionsBid = a.getBidIds();
			UserDAO u = CosmosDBLayer.getInstance().getUserById(bid.getUser());
			Set<String> userBids = u.getBidsIds();
			CosmosItemResponse<BidDAO> res = null;
			if(Float.valueOf(bid.getValue()) > Float.valueOf(a.getMinPrice()) && a.getStatus().equals("OPEN")){
				auctionsBid.add(bid.getId());
				a.setBidIds(auctionsBid);
				a.setMinPrice(bid.getValue());
				a.setLastBid(uid);
				resAuction (CosmosDBLayer.getInstance().updateAuction(a));
				userBids.add(uid);
				u.setBidsIds(userBids);
				UsersResource.updateUser(session, u);
				res = resBid (CosmosDBLayer.getInstance().putBid(bid));
				

			}
			//if(res == null) {
				//throw new BadRequestException("Bid is less than min price");
			//}
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new NotFoundException();
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

	@POST
	@Path ("/{auctionId}/question")
	@Consumes(MediaType.APPLICATION_JSON) //novo
	@Produces(MediaType.APPLICATION_JSON)
	public QuestionDAO putQuestion (@CookieParam("scc:session") Cookie session, @PathParam("auctionId") String id, QuestionDAO question) {
		try {
			UsersResource.checkCookieUser(session, question.getUserId());
			AuctionDAO a = CosmosDBLayer.getInstance().getAuctionById(id);
			String uid = UUID.randomUUID().toString();
			question.setId(uid);
			CosmosItemResponse<QuestionDAO> res = CosmosDBLayer.getInstance().putQuestion(question);
			Map<String, Set<String>> questionsIds = a.getQuestionsIds();
			Set<String> s = new HashSet<String>();
			questionsIds.put(question.getId(),s);
			a.setQuestionsIds(questionsIds);
			CosmosDBLayer.getInstance().updateAuction(a);
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}
	}

	@POST
	@Path ("/{auctionId}/question/{questionId}/reply")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public QuestionDAO putReply (@CookieParam("scc:session") Cookie session, @PathParam("auctionId") String auctionId, @PathParam("questionId") String questionId, QuestionDAO reply) {
		try {
			UsersResource.checkCookieUser(session, reply.getUserId());
			AuctionDAO a = CosmosDBLayer.getInstance().getAuctionById(auctionId);
			String uid = UUID.randomUUID().toString();
			reply.setId(uid);
			CosmosItemResponse<QuestionDAO> res = CosmosDBLayer.getInstance().putQuestion(reply);
			Map<String, Set<String>> questionsIds = a.getQuestionsIds();
			Set<String> replys = questionsIds.get(questionId);
			replys.add(uid);
			questionsIds.replace(questionId, replys);
			a.setQuestionsIds(questionsIds);
			CosmosDBLayer.getInstance().updateAuction(a);
			return res.getItem();
		} catch( NotAuthorizedException e) {
			throw e;
		} catch( Exception e) {
			throw new BadRequestException();
		}
	}

	@GET
	@Path("/{auctionId}/question")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<QuestionDAO> listQuestions (@PathParam("auctionId") String id) {
		Set<QuestionDAO> aux = new HashSet<QuestionDAO>();
		AuctionDAO a = CosmosDBLayer.getInstance().getAuctionById(id);
		Set<String> questionIds  = a.getQuestionsIds().keySet();
		for (String x: questionIds ) {
			QuestionDAO q = CosmosDBLayer.getInstance().getQuestionById(x);
			aux.add(q);
		}
		return aux;
	}

	@GET
	@Path("/aboutToClose")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<AuctionDAO> listAuctionsAboutClose () {
		Set<AuctionDAO> allAuctions = CosmosDBLayer.getInstance().getAllAuctions();
		Set<AuctionDAO> setAuctionsClose = new HashSet<AuctionDAO>();
		Date currentTime = getDateTime(java.time.LocalDateTime.now().toString().split("[.]") [0]);
		for (AuctionDAO a : allAuctions) {
			Date auctionTime = getDateTime(a.getEndTime().split("[.]") [0]);
			long differenceMs = auctionTime.getTime() - currentTime.getTime();
			double differenceMinutes =  ((double) differenceMs / 1000) / 60;
			if(differenceMinutes < 30.0 && a.getStatus().equals("OPEN")) {
				setAuctionsClose.add(a);
			}
		}
		return setAuctionsClose;
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

	private Date getDateTime(String d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date time = null;
		try {
			time = sdf.parse(d);
		} catch (ParseException e) {
		}
		return time;
	} 
}
