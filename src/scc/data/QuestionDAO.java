package scc.data;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String auctionId;
    private String userId;
    private String text;

    public QuestionDAO(){
    }

    public QuestionDAO(Question q){
        this(q.getId(), q.getAuctionId(), q.getUserId(), q.getText());
    }

    public QuestionDAO( String id, String auctionId, String userId, String text) {
        super();
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.text = text;
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
