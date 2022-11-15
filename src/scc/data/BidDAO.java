package scc.data;

public class BidDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String auctionId;
    private String user;
    private String value;


    public BidDAO(){

    }

    public BidDAO(Bid a){
        this(a.getId(), a.getAuctionId(), a.getUser(), a.getValue());
    }

    public BidDAO(String id, String auctionId, String user, String value){
        super();
        this.id = id;
        this.auctionId = auctionId;
        this.user = user;
        this.value = value;
    }

    public String get_rid() {
        return _rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "AuctionDAO{" +
                "_rid='" + _rid + '\'' +
                ", _ts='" + _ts + '\'' +
                ", id='" + id + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", user='" + user + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
