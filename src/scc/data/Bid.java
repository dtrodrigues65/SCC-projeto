package scc.data;

public class Bid {
    private String id;
    private String auctionId;
    private String user;
    private String value;

    public Bid(String id, String auctionId, String user, String value){
        super();
        this.id = id;
        this.auctionId = auctionId;
        this.user = user;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuctionId(){
        return auctionId;
    }

    public void setAuctionId(String auctionId){
        this.auctionId = auctionId;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value =  value;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id='" + id + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", user='" + user + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}
