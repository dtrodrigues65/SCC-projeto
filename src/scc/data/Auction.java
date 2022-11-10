package scc.data;

public class Auction {
    private String id;
    private String title;
    private String description;
    private String photoId;
    private String user;
    private String endTime;
    private String minPrice;
    private String winnerBid;
    private String status;
    public Auction(String id, String title, String description, String photoId, String user,
                   String endTime, String minPrice, String winnerBid, String status){
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.photoId = photoId;
        this.user = user;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.winnerBid = winnerBid;
        this.status = status;
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

    public String getWinnerBid() {
        return winnerBid;
    }

    public void setWinnerBid(String winnerBid) {
        this.winnerBid = winnerBid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", photoId='" + photoId + '\'' +
                ", user='" + user + '\'' +
                ", endTime='" + endTime + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", winnerBid='" + winnerBid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
