package scc.data;

public class Question {
    private String id;
    private String auctionId;
    private String userId;
    private String text;

    public Question(String id, String auctionId, String userId, String text){
        super();
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
