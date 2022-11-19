package scc.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Auction {
    private String id;
    private String title;
    private String description;
    private String photoId;
    private String user;
    private String endTime;
    private String minPrice;
    private String lastBid;
    private String winnerBid;
    private Set<String> listBids;
    private String status;
    private Map<String, Set<String>> listQuestions;
    

    public Auction(String id, String title, String description, String photoId, String user,
                   String endTime, String minPrice, String lastBid, String winnerBid, Set<String> listBids, String status,Map<String, Set<String>> listQuestions ){
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.photoId = photoId;
        this.user = user;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.lastBid = lastBid;
        this.winnerBid = winnerBid;
        this.listBids = listBids;
        this.status = status;
        this.listQuestions = listQuestions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getLastBid() {
        return lastBid;
    }

    public void setLastBid(String lastBid) {
        this.lastBid = lastBid;
    }

    public String getWinnerBid() {
        return winnerBid;
    }

    public void setWinnerBid(String winnerBid) {
        this.winnerBid = winnerBid;
    }

    public Set<String> getBidIds() {
		return listBids == null ? new HashSet<String>() : listBids;
	}
	public void setBidIds(Set<String> listBids) {
		this.listBids = listBids;
	}

    public Map<String, Set<String>> getQuestionsIds() {
		return listQuestions == null ? new HashMap<String, Set<String>>() : listQuestions;
	}
	public void setQuestionsIds(Map<String, Set<String>> listQuestions) {
		this.listQuestions = listQuestions;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
